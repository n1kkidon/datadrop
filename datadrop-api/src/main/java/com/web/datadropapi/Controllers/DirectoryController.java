package com.web.datadropapi.Controllers;

import com.web.datadropapi.Enums.SharedState;
import com.web.datadropapi.Handler.Exception.DuplicateDataException;
import com.web.datadropapi.Mappers.FileMapperService;
import com.web.datadropapi.Mappers.UserMapperService;
import com.web.datadropapi.Models.DirectoryDto;
import com.web.datadropapi.Models.NameUpdateRequest;
import com.web.datadropapi.Models.ShareStateUpdateRequest;
import com.web.datadropapi.Models.UserDto;
import com.web.datadropapi.Repositories.*;
import com.web.datadropapi.Repositories.Entities.DirectoryEntity;
import com.web.datadropapi.Repositories.Entities.FileEntity;
import com.web.datadropapi.Repositories.Entities.SharedDirectoryEntity;
import com.web.datadropapi.Repositories.Entities.SharedFileEntity;
import com.web.datadropapi.Utils.FileUploadUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

@RestController
@RequiredArgsConstructor
@RequestMapping("/directory")
@Validated
public class DirectoryController {
    @Autowired
    private FileRepository fileRepository;
    @Autowired
    private DirectoryRepository directoryRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    FileMapperService fileMapperService;

    @Autowired
    private SharedDirectoryRepository sharedDirectoryRepository;
    @Autowired
    private SharedFileRepository sharedFileRepository;
    @Autowired
    private UserMapperService userMapperService;

    @PostMapping("/post/create")
    public ResponseEntity<DirectoryDto> createDirectory(@RequestBody DirectoryDto request) throws IOException {
        var opt = userRepository.findById(request.getOwnerId());
        if(opt.isEmpty())
            throw new NoSuchElementException("the user with the given id doesn't exist.");
        var entity = new DirectoryEntity();
        entity.setCreationDate(request.getCreationDate()); //TODO: serverside
        entity.setLastModifiedDate(request.getLastModifiedDate());

        if(request.getParentDirectoryId() != null){
            var directory = directoryRepository.findById(request.getParentDirectoryId());
            if(directory.isEmpty())
                throw new NoSuchElementException("Requested destination directory not found.");
            var parent = directory.get();
            if(parent.getFiles().stream().anyMatch(x -> Objects.equals(x.getName(), request.getName())) ||
                    parent.getSubdirectories().stream().anyMatch(x -> Objects.equals(x.getName(), request.getName()))){
                throw new DuplicateDataException(String.format("Requested destination directory already has a file named '%s'.",
                        request.getName()));
            }

            entity.setParentDirectory(parent);
        }
        else {
            var rootDir = directoryRepository.findByOwner_idAndParentDirectory_IdIsNull(request.getOwnerId());
            if(!rootDir.isEmpty())
                throw new DuplicateDataException("Users root directory already exists. Specify parentDirectoryId.");
        }

        entity.setName(request.getName());
        entity.setOwner(opt.get()); //TODO: this should be fixed to get a user from jwt token
        entity.setSharedState(request.getSharedState());
        directoryRepository.save(entity);
        FileUploadUtils.createFolderIfDoesntExist(entity);
        return new ResponseEntity<>(fileMapperService.mapDirectoryEntityToDto(entity), HttpStatus.OK);
    }
    @PatchMapping("/patch/rename") //TODO: move duplicate code to a service class
    public ResponseEntity<DirectoryDto> modifyDirectoryName(@RequestBody NameUpdateRequest request) throws IOException {
        var file = directoryRepository.findById(request.getItemId());
        if(file.isEmpty())
            throw new NoSuchElementException("Requested directory was not found.");

        var foundFile = file.get();
        var directory = foundFile.getParentDirectory();
        var resource = directory.getChildItemInSystem(foundFile.getName());
        if(resource.exists() && resource.getFile().isDirectory())
        {
            if(foundFile.getParentDirectory().containsItem(request.getNewName())){
                throw new DuplicateDataException(String.format("filename %s already exists in %s", request.getNewName(), foundFile.getParentDirectory().getAbsolutePath()));
            }
            if(resource.getFile().renameTo(new File(resource.getFile().getParentFile().getPath()+ "/" + request.getNewName()))) {
                foundFile.setName(request.getNewName());
                directoryRepository.save(foundFile);
                return new ResponseEntity<>(fileMapperService.mapDirectoryEntityToDto(foundFile), HttpStatus.OK);
            }
            else throw new SecurityException("Could not rename the requested file.");

        }
        else throw new NoSuchElementException("Could not find the directory in the server");
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteDirectoryById(@PathVariable("id") Long id) throws IOException {
        var file = directoryRepository.findById(id);
        if(file.isEmpty())
            throw new NoSuchElementException("Requested directory was not found.");

        var foundFile = file.get();
        var resource = foundFile.getParentDirectory().getChildItemInSystem(foundFile.getName());
        if(resource.exists()){
            System.out.println(resource.getFile().isDirectory());
            if(resource.getFile().isDirectory() && FileUploadUtils.deleteFolder(resource.getFile())){
                directoryRepository.delete(foundFile);
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            else throw new SecurityException("Could not access the directory in the server"); //not sufficient permissions in the server OS
        }
        else throw new NoSuchElementException("Could not find the directory in the server");
    }

    @GetMapping("/get/{id}/info")
    public ResponseEntity<DirectoryDto> getDirectoryById(@PathVariable("id") Long id) {
        var file = directoryRepository.findById(id);
        if(file.isEmpty())
            throw new NoSuchElementException("Requested directory was not found.");

        var foundFile = file.get();
        return new ResponseEntity<>(fileMapperService.mapDirectoryEntityToDto(foundFile), HttpStatus.OK);
    }

    @PatchMapping("/patch/share") //TODO: make an option to share every item in the directory recursively
    public ResponseEntity<Void> changeDirectoryShareState(@RequestBody ShareStateUpdateRequest request){
        var file = directoryRepository.findById(request.getItemId());
        if(file.isEmpty())
            throw new NoSuchElementException("Requested directory was not found.");

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
                    throw new DuplicateDataException(String.format("You are already sharing this directory with userId %s", userId));
                var temp = new SharedDirectoryEntity();
                temp.setDirectory(foundFile);
                temp.setSharedWith(user.get());
                sharedDirectoryRepository.save(temp);
            }
        }
        else sharedDirectoryRepository.deleteAllInBatch(foundFile.getSharedWith());

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/get/shared/{id}")
    public ResponseEntity<List<UserDto>> getUsersFileIsSharedWith(@PathVariable("id") Long id){
        var file = directoryRepository.findById(id);
        if(file.isEmpty())
            throw new NoSuchElementException("Requested directory was not found.");
        var foundFile = file.get();
        var sharedWith = foundFile.getSharedWith().stream().map(SharedDirectoryEntity::getSharedWith).toList();
        return new ResponseEntity<>(userMapperService.mapUserEntitiesToDtos(sharedWith), HttpStatus.OK);
    }
}
