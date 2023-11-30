package com.web.datadropapi.Controllers;

import com.web.datadropapi.Enums.SharedState;
import com.web.datadropapi.Handler.Exception.DuplicateDataException;
import com.web.datadropapi.Mappers.FileMapperService;
import com.web.datadropapi.Mappers.UserMapperService;
import com.web.datadropapi.Models.DirectoryDto;
import com.web.datadropapi.Models.Requests.NameUpdateRequest;
import com.web.datadropapi.Models.Requests.ShareStateUpdateRequest;
import com.web.datadropapi.Models.UserDto;
import com.web.datadropapi.Repositories.DirectoryRepository;
import com.web.datadropapi.Repositories.Entities.DirectoryEntity;
import com.web.datadropapi.Repositories.Entities.SharedDirectoryEntity;
import com.web.datadropapi.Repositories.SharedDirectoryRepository;
import com.web.datadropapi.Repositories.UserRepository;
import com.web.datadropapi.Services.DirectoryService;
import com.web.datadropapi.Services.UserService;
import com.web.datadropapi.Utils.FileUploadUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/directory")
@Validated
public class DirectoryController {
    @Autowired
    private DirectoryRepository directoryRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private FileMapperService fileMapperService;

    @Autowired
    private SharedDirectoryRepository sharedDirectoryRepository;
    @Autowired
    private UserMapperService userMapperService;

    @Autowired
    private UserService userService;
    @Autowired
    private DirectoryService directoryService;

    @PostMapping("/create")
    public ResponseEntity<DirectoryDto> createDirectory(@RequestBody DirectoryDto request) throws IOException {
        var user = userService.getCurrentUser();

        var entity = new DirectoryEntity();
        entity.setCreationDate(LocalDate.now());
        entity.setLastModifiedDate(LocalDate.now());

        DirectoryEntity parent;
        if(request.getParentDirectoryId() == null)
            parent = userService.getCurrentUserRootDirectory();
        else parent = directoryService.getUserAccessibleDirectory(request.getParentDirectoryId());

        if(parent.containsItem(request.getName())){
            throw new DuplicateDataException(String.format("filename %s already exists in %s", request.getName(), parent.getAbsolutePath()));
        }

        entity.setParentDirectory(parent);
        entity.setName(request.getName());
        entity.setOwner(user);
        entity.setSharedState(request.getSharedState());
        directoryRepository.save(entity);
        FileUploadUtils.createFolderIfDoesntExist(entity);
        return new ResponseEntity<>(fileMapperService.mapDirectoryEntityToDto(entity), HttpStatus.OK);
    }
    @PatchMapping("/rename")
    public ResponseEntity<DirectoryDto> modifyDirectoryName(@RequestBody NameUpdateRequest request) throws IOException {
        var dir = directoryService.getUserAccessibleDirectory(request.getItemId());
        dir = directoryService.renameDirectory(dir, request.getNewName());
        return new ResponseEntity<>(fileMapperService.mapDirectoryEntityToDto(dir), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDirectoryById(@PathVariable("id") Long id) throws IOException {
        var directory = directoryService.getUserAccessibleDirectory(id);

        directoryService.deleteDirectory(directory);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/{id}/info")
    public ResponseEntity<DirectoryDto> getDirectoryById(@PathVariable("id") Long id) {
        DirectoryEntity directory;
        if (id == 0){
            directory = userService.getCurrentUserRootDirectory();
        }
        else directory = directoryService.getUserAccessibleDirectory(id);
        return new ResponseEntity<>(fileMapperService.mapDirectoryEntityToDto(directory), HttpStatus.OK);
    }

    @PatchMapping("/share") //TODO: make an option to share every item in the directory recursively
    public ResponseEntity<Void> changeDirectoryShareState(@RequestBody ShareStateUpdateRequest request){
        var directory = directoryService.getUserAccessibleDirectory(request.getItemId());

        directory.setSharedState(request.getState());
        if(request.getState().equals(SharedState.SHARED)){
            var userIds = request.getUserIds();
            var sharedWith = directory.getSharedWith();
            for(var userId : userIds){
                var user = userRepository.findById(userId);
                if(user.isEmpty())
                    throw new NoSuchElementException(String.format("Requested user with id %s was not found.", userId));
                if(sharedWith.stream().anyMatch(x -> x.getSharedWith().getId().equals(userId)))
                    throw new DuplicateDataException(String.format("You are already sharing this directory with userId %s", userId));
                var temp = new SharedDirectoryEntity();
                temp.setDirectory(directory);
                temp.setSharedWith(user.get());
                sharedDirectoryRepository.save(temp);
            }
        }
        else sharedDirectoryRepository.deleteAllInBatch(directory.getSharedWith());

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/shared/{id}")
    public ResponseEntity<List<UserDto>> getUsersFileIsSharedWith(@PathVariable("id") Long id){
        var directory = directoryService.getUserAccessibleDirectory(id);

        var sharedWith = directory.getSharedWith().stream().map(SharedDirectoryEntity::getSharedWith).toList();
        return new ResponseEntity<>(userMapperService.mapUserEntitiesToDtos(sharedWith), HttpStatus.OK);
    }
}
