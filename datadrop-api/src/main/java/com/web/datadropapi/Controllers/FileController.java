package com.web.datadropapi.Controllers;

import com.web.datadropapi.Enums.SharedState;
import com.web.datadropapi.Handler.Exception.DuplicateDataException;
import com.web.datadropapi.Mappers.FileMapperService;
import com.web.datadropapi.Mappers.UserMapperService;
import com.web.datadropapi.Models.*;
import com.web.datadropapi.Models.Requests.NameUpdateRequest;
import com.web.datadropapi.Models.Requests.ShareStateUpdateRequest;
import com.web.datadropapi.Models.Requests.UploadRequest;
import com.web.datadropapi.Repositories.DirectoryRepository;
import com.web.datadropapi.Repositories.Entities.DirectoryEntity;
import com.web.datadropapi.Repositories.Entities.FileEntity;
import com.web.datadropapi.Repositories.Entities.SharedFileEntity;
import com.web.datadropapi.Repositories.FileRepository;
import com.web.datadropapi.Repositories.SharedFileRepository;
import com.web.datadropapi.Repositories.UserRepository;
import com.web.datadropapi.Services.DirectoryService;
import com.web.datadropapi.Services.FileService;
import com.web.datadropapi.Services.UserService;
import com.web.datadropapi.Utils.FileUploadUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
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

    @Autowired
    private UserService userService;
    @Autowired
    private DirectoryService directoryService;
    @Autowired
    private FileService fileService;

    @PostMapping("/upload")
    public ResponseEntity<FileDto> uploadFile(@Param("file") MultipartFile file, UploadRequest request) throws IOException {
        if(file == null)
            throw new InputMismatchException("No file attached.");

        var entity = new FileEntity();
        entity.setCreationDate(LocalDate.now());
        entity.setName(file.getOriginalFilename());
        entity.setSharedState(SharedState.PRIVATE);

        DirectoryEntity directory;
        if(request.getUploadDirectoryId() == null)
            directory = userService.getCurrentUserRootDirectory();
        else directory = directoryService.getUserAccessibleDirectory(request.getUploadDirectoryId()); //testing userAccessible

        if(directory.containsItem(file.getOriginalFilename()))
            throw new DuplicateDataException(String.format("filename %s already exists in %s", file.getOriginalFilename(), directory.getAbsolutePath()));

        entity.setParentDirectory(directory);
        entity.setLastModifiedDate(LocalDate.now());
        fileRepository.save(entity);
        FileUploadUtils.saveFileToSystem(directory, file);

        return new ResponseEntity<>(fileMapperService.mapFileEntityToDto(entity), HttpStatus.OK);
    }

    @PatchMapping("/rename")
    public ResponseEntity<FileDto> updateFileById(@RequestBody NameUpdateRequest request) throws IOException {
        var file = fileService.getUserAccessibleFileById(request.getItemId());
        file = fileService.renameFile(file, request.getNewName());
        return new ResponseEntity<>(fileMapperService.mapFileEntityToDto(file), HttpStatus.OK);
    }

    @GetMapping("/{id}/info")
    public ResponseEntity<FileDto> getFileInfoById(@PathVariable("id") Long id){
        var file = fileService.getUserAccessibleFileById(id);
        return new ResponseEntity<>(fileMapperService.mapFileEntityToDto(file), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Resource> downloadFileById(@PathVariable("id") Long id) throws MalformedURLException {
        var file = fileService.getUserAccessibleFileById(id);

        var resource = file.getParentDirectory().getChildItemInSystem(file.getName());
        if(resource.exists()){
            if(resource.isReadable()){
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + file.getName())
                        .contentType(MediaType.APPLICATION_OCTET_STREAM)
                        .body(resource);
            }
            else throw new SecurityException("Could not access the file in the server"); //not sufficient permissions in the server OS
        }
        else throw new NoSuchElementException("Could not find the file in the server");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFileById(@PathVariable("id") Long id) throws IOException {
        var file = fileService.getUserAccessibleFileById(id);

        fileService.deleteFile(file);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PatchMapping("/share")
    public ResponseEntity<Void> changeFileShareState(@RequestBody ShareStateUpdateRequest request){
        var file = fileService.getUserAccessibleFileById(request.getItemId());

        file.setSharedState(request.getState());
        if(request.getState().equals(SharedState.SHARED)){
            var userIds = request.getUserIds();
            var sharedWith = file.getSharedWith();
            for(var userId : userIds){
                var user = userRepository.findById(userId);
                if(user.isEmpty())
                    throw new NoSuchElementException(String.format("Requested user with id %s was not found.", userId));
                if(sharedWith.stream().anyMatch(x -> x.getSharedWith().getId().equals(userId)))
                    throw new DuplicateDataException(String.format("You are already sharing this file with userId %s", userId));
                var temp = new SharedFileEntity();
                temp.setDirectory(file);
                temp.setSharedWith(user.get());
                sharedFileRepository.save(temp);
            }
        }
        else sharedFileRepository.deleteAllInBatch(file.getSharedWith());

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/shared/{id}")
    public ResponseEntity<List<UserDto>> getUsersFileIsSharedWith(@PathVariable("id") Long id){
        var file = fileService.getUserAccessibleFileById(id);

        var sharedWith = userService.getUsersFileIsSharedWith(file);
        return new ResponseEntity<>(userMapperService.mapUserEntitiesToDtos(sharedWith), HttpStatus.OK);
    }

}
