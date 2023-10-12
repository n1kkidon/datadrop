package com.web.datadropapi.Controllers;

import com.web.datadropapi.Enums.SharedState;
import com.web.datadropapi.Handler.Exception.DuplicateDataException;
import com.web.datadropapi.Mappers.FileMapperService;
import com.web.datadropapi.Mappers.UserMapperService;
import com.web.datadropapi.Models.*;
import com.web.datadropapi.Repositories.DirectoryRepository;
import com.web.datadropapi.Repositories.Entities.DirectoryEntity;
import com.web.datadropapi.Repositories.Entities.FileEntity;
import com.web.datadropapi.Repositories.Entities.SharedFileEntity;
import com.web.datadropapi.Repositories.FileRepository;
import com.web.datadropapi.Repositories.SharedFileRepository;
import com.web.datadropapi.Repositories.UserRepository;
import com.web.datadropapi.Utils.FileUploadUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.InputMismatchException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

@RestController
@RequiredArgsConstructor
@RequestMapping("/file")
@Validated
public class FileController {
    @Autowired
    private FileRepository fileRepository;
    @Autowired
    private DirectoryRepository directoryRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private SharedFileRepository sharedFileRepository;

    @Autowired
    private FileMapperService fileMapperService;
    @Autowired
    private UserMapperService userMapperService;

    @PostMapping("/post/upload")
    public ResponseEntity<FileDto> uploadFile(@Param("file") MultipartFile file, UploadRequest request) throws IOException {
        if(file == null)
            throw new InputMismatchException("No file attached.");
        else if(request.getUserId() == null){
            throw new InputMismatchException("No user id specified."); //TODO: read user from jwt token
        }
        var entity = new FileEntity();
        entity.setCreationDate(LocalDate.now());
        entity.setName(file.getOriginalFilename());
        entity.setSharedState(SharedState.PRIVATE);

        DirectoryEntity directory;
        if(request.getUploadDirectoryId() == null) {
            var user = userRepository.findById(request.getUserId());
            if(user.isEmpty()){
                throw new NoSuchElementException(String.format("User with the id %s was not found.", request.getUserId()));
            }
            var userRoot = directoryRepository.findByOwner_idAndParentDirectory_IdIsNull(request.getUserId());
            if(userRoot.isEmpty()){
                directory = new DirectoryEntity();
                directory.setCreationDate(LocalDate.now());
                directory.setOwner(user.get());
                directory.setSharedState(SharedState.PRIVATE);
                directory.setLastModifiedDate(LocalDate.now());
                directoryRepository.save(directory);
            }
            else directory = userRoot.getFirst();

        }
        else {
            var optionalDirectory = directoryRepository.findById(request.getUploadDirectoryId());
            if (optionalDirectory.isEmpty() || !Objects.equals(optionalDirectory.get().getOwner().getId(), request.getUserId()))
                throw new NoSuchElementException("Requested destination directory not found.");
            else directory = optionalDirectory.get();
        }
        if(directory.containsItem(file.getOriginalFilename()))
            throw new DuplicateDataException(String.format("filename %s already exists in %s", file.getOriginalFilename(), directory.getAbsolutePath()));
        entity.setParentDirectory(directory);
        entity.setLastModifiedDate(LocalDate.now());
        fileRepository.save(entity);
        FileUploadUtils.saveFileToSystem(directory, file);

        return new ResponseEntity<>(fileMapperService.mapFileEntityToDto(entity), HttpStatus.OK);
    }

    @PatchMapping("/patch/rename")
    public ResponseEntity<FileDto> updateFileById(@RequestBody NameUpdateRequest request) throws IOException { //TODO: validate
        var file = fileRepository.findById(request.getItemId());
        if(file.isEmpty())
            throw new NoSuchElementException("Requested file was not found.");

        var foundFile = file.get();
        var resource = foundFile.getParentDirectory().getChildItemInSystem(foundFile.getName());
        if(resource.exists() && resource.isReadable())
        {
            if(foundFile.getParentDirectory().containsItem(request.getNewName())){
                throw new DuplicateDataException(String.format("filename %s already exists in %s", request.getNewName(), foundFile.getParentDirectory().getAbsolutePath()));
            }
            if(resource.getFile().renameTo(new File(resource.getFile().getParentFile().getPath()+ "/" + request.getNewName()))) {
                foundFile.setName(request.getNewName());
                fileRepository.save(foundFile);
                return new ResponseEntity<>(fileMapperService.mapFileEntityToDto(foundFile), HttpStatus.OK);
            }
            else throw new SecurityException("Could not rename the requested file.");

        }
        else throw new NoSuchElementException("Could not find the file in the server");
    }

    @GetMapping("/get/{id}/info")
    public ResponseEntity<FileDto> getFileInfoById(@PathVariable("id") Long id){ //TODO: based on userId from jwt, adjust file visibility
        var file = fileRepository.findById(id);
        if(file.isEmpty())
            throw new NoSuchElementException("Requested file was not found.");

        var foundFile = file.get();
        return new ResponseEntity<>(fileMapperService.mapFileEntityToDto(foundFile), HttpStatus.OK);
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<Resource> downloadFileById(@PathVariable("id") Long id) throws MalformedURLException {
        var file = fileRepository.findById(id);
        if(file.isEmpty())
            throw new NoSuchElementException("Requested file was not found.");

        var foundFile = file.get();
        var resource = foundFile.getParentDirectory().getChildItemInSystem(foundFile.getName());
        if(resource.exists()){
            if(resource.isReadable()){
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + foundFile.getName())
                        .contentType(MediaType.APPLICATION_OCTET_STREAM)
                        .body(resource);
            }
            else throw new SecurityException("Could not access the file in the server"); //not sufficient permissions in the server OS
        }
        else throw new NoSuchElementException("Could not find the file in the server");
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteFileById(@PathVariable("id") Long id) throws IOException { //TODO: delete empty folders in the system
        var file = fileRepository.findById(id);
        if(file.isEmpty())
            throw new NoSuchElementException("Requested file was not found.");

        var foundFile = file.get();
        var resource = foundFile.getParentDirectory().getChildItemInSystem(foundFile.getName());
        if(resource.exists()){
            if(resource.isReadable() && resource.getFile().delete()){
                fileRepository.delete(foundFile);
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            else throw new SecurityException("Could not access the file in the server"); //not sufficient permissions in the server OS
        }
        else throw new NoSuchElementException("Could not find the file in the server");
    }

    @PatchMapping("/patch/share")
    public ResponseEntity<Void> changeFileShareState(@RequestBody ShareStateUpdateRequest request){
        var file = fileRepository.findById(request.getItemId());
        if(file.isEmpty())
            throw new NoSuchElementException("Requested file was not found.");

        var foundFile = file.get();
        foundFile.setSharedState(request.getState());
        if(request.getState().equals(SharedState.SHARED)){
            var userIds = request.getUserIds();
            var sharedWith = foundFile.getSharedWith();
            for(var userId : userIds){
                var user = userRepository.findById(userId);
                if(user.isEmpty())
                    throw new NoSuchElementException(String.format("Requested user with id %s was not found.", userId));
                if(sharedWith.stream().anyMatch(x -> x.getSharedWith().getId().equals(userId)))
                    throw new DuplicateDataException(String.format("You are already sharing this file with userId %s", userId));
                var temp = new SharedFileEntity();
                temp.setDirectory(foundFile);
                temp.setSharedWith(user.get());
                sharedFileRepository.save(temp);
            }
        }
        else sharedFileRepository.deleteAllInBatch(foundFile.getSharedWith());

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/get/shared/{id}")
    public ResponseEntity<List<UserDto>> getUsersFileIsSharedWith(@PathVariable("id") Long id){
        var file = fileRepository.findById(id);
        if(file.isEmpty())
            throw new NoSuchElementException("Requested file was not found.");
        var foundFile = file.get();
        var sharedWith = foundFile.getSharedWith().stream().map(SharedFileEntity::getSharedWith).toList();
        return new ResponseEntity<>(userMapperService.mapUserEntitiesToDtos(sharedWith), HttpStatus.OK);
    }

}
