package com.web.datadropapi.Controllers;

import com.web.datadropapi.Enums.SharedState;
import com.web.datadropapi.Enums.UserRole;
import com.web.datadropapi.Enums.UserState;
import com.web.datadropapi.Handler.Exception.DuplicateDataException;
import com.web.datadropapi.Mappers.FileMapperService;
import com.web.datadropapi.Mappers.UserMapperService;
import com.web.datadropapi.Models.*;
import com.web.datadropapi.Repositories.DirectoryRepository;
import com.web.datadropapi.Repositories.Entities.SharedFileEntity;
import com.web.datadropapi.Repositories.Entities.SharedDirectoryEntity;
import com.web.datadropapi.Repositories.Entities.UserEntity;
import com.web.datadropapi.Repositories.UserRepository;
import com.web.datadropapi.Utils.FileUploadUtils;
import jakarta.persistence.Table;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
@Validated
public class UserController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserMapperService userMapperService;
    @Autowired
    private FileMapperService fileMapperService;

    @Autowired
    private DirectoryRepository directoryRepository;
    @PostMapping("/register")
    public ResponseEntity<Void> registerNewUser(@Valid @RequestBody RegistrationRequest request){
        var opt = userRepository.findByName(request.getUsername());
        if(opt.isPresent())
            return new ResponseEntity<>(HttpStatus.CONFLICT);

        var user = new UserEntity();
        user.setName(request.getUsername());
        user.setPassword(request.getPassword()); //TODO: hash this thing
        user.setCreationDate(LocalDate.now());
        user.setLastModifiedDate(LocalDate.now());
        user.setRole(UserRole.ROLE_USER);
        user.setEmail(request.getEmail());
        user.setState(UserState.ACTIVE);
        userRepository.save(user);

        return new ResponseEntity<>(HttpStatus.CREATED);
    }
    @PostMapping("/login")
    public ResponseEntity<String> login(@Valid @RequestBody LoginRequest request){
        var opt = userRepository.findByName(request.getUsername());
        if(opt.isEmpty())
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        var user = opt.get();
        if(request.getPassword().equals(user.getPassword()))
            return ResponseEntity.ok("token response here");
        else return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    @GetMapping("/get/all")
    public ResponseEntity<List<UserDto>> getAllUsers(){
        var users = userRepository.findAll();
        var userDtos = userMapperService.mapUserEntitiesToDtos(users);

        return ResponseEntity.ok(userDtos);
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable("id") Long id){
        var user = userRepository.findById(id);
        return user.map(userEntity ->
                ResponseEntity.ok(userMapperService.mapUserEntityToDto(userEntity))).orElseGet(() ->
                new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/get/shared/files/{id}")
    public ResponseEntity<List<FileDto>> getFilesSharedWithUser(@PathVariable("id") Long id){
        var user = userRepository.findById(id);
        if(user.isEmpty())
            throw new NoSuchElementException("Requested user was not found.");
        var foundUser = user.get();
        var sharedWith = foundUser.getSharedFilesWithUser().stream().map(SharedFileEntity::getDirectory).toList();

        return new ResponseEntity<>(fileMapperService.mapFileEntitiesToDtos(sharedWith), HttpStatus.OK);
    }

    @GetMapping("/get/shared/directories/{id}")
    public ResponseEntity<List<DirectoryDto>> getDirectoriesSharedWithUser(@PathVariable("id") Long id){
        var user = userRepository.findById(id);
        if(user.isEmpty())
            throw new NoSuchElementException("Requested user was not found.");
        var foundUser = user.get();
        var sharedWith = foundUser.getSharedDirectoriesWithUser().stream().map(SharedDirectoryEntity::getDirectory).toList();

        return new ResponseEntity<>(fileMapperService.mapDirectoryEntitiesToDtos(sharedWith), HttpStatus.OK);
    }

    @PutMapping("/put/account")
    public ResponseEntity<AccountUpdateResponse> putAccountDetails(@Valid @RequestBody AccountUpdateRequest request){
        var opt = userRepository.findById(request.getId());
        if(opt.isEmpty())
            throw new NoSuchElementException("Account with the given id was not found.");

        var user = opt.get();
        if(!request.getPassword().equals(user.getPassword()))
            throw new SecurityException("Incorrect password.");

        user.setName(request.getUsername());
        user.setPassword(request.getNewPassword()); //TODO: hash this thing
        user.setLastModifiedDate(LocalDate.now());
        user.setEmail(request.getEmail());
        userRepository.save(user);
        var resp = new AccountUpdateResponse(user.getId(), user.getEmail(), user.getName());

        return new ResponseEntity<>(resp, HttpStatus.OK);
    }
    @DeleteMapping("/delete/account/{id}")
    public ResponseEntity<Void> deleteUserAccountById(@PathVariable("id") Long id) throws IOException {
        var opt = userRepository.findById(id);
        if(opt.isEmpty())
            throw new NoSuchElementException("Account with the given id was not found.");

        var user = opt.get();
        var rootDir = directoryRepository.findByOwner_idAndParentDirectory_IdIsNull(user.getId());
        if(rootDir != null && !rootDir.isEmpty()){
            var path = Path.of(rootDir.getFirst().getAbsolutePath());
            path = Paths.get("USER_FILES/" + user.getId(), path.toString());
            var resource = new UrlResource(path.toUri());
            var success = FileUploadUtils.deleteFolder(resource.getFile());
            if(success){
                userRepository.delete(user);
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            else throw new SecurityException("Could not delete user files.");
        }
        userRepository.delete(user);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    @PatchMapping("/patch/block/{id}")
    public ResponseEntity<UserDto> changeBlockStateOfUserAccountById(@PathVariable("id") Long id){
        var opt = userRepository.findById(id);
        if(opt.isEmpty())
            throw new NoSuchElementException("Account with the given id was not found.");

        var user = opt.get();
        if(user.getState() == UserState.ACTIVE)
            user.setState(UserState.BANNED);
        else user.setState(UserState.ACTIVE);
        userRepository.save(user);
        return new ResponseEntity<>(userMapperService.mapUserEntityToDto(user), HttpStatus.OK);
    }

    @GetMapping("/get/storage/{id}")
    public ResponseEntity<SpaceUsageResponse> getSpaceUsageStatsByUserId(@PathVariable("id") Long id) throws IOException {
        var opt = userRepository.findById(id);
        double MAX_SPACE = 20;
        if(opt.isEmpty())
            throw new NoSuchElementException("Account with the given id was not found.");

        var user = opt.get();
        var rootDir = directoryRepository.findByOwner_idAndParentDirectory_IdIsNull(user.getId()); //TODO: change the data method to return 1 item
        if(rootDir == null || rootDir.isEmpty()){
            return new ResponseEntity<>(new SpaceUsageResponse(0, MAX_SPACE, MAX_SPACE), HttpStatus.OK);
        }
        else {
            var path = Path.of(rootDir.getFirst().getAbsolutePath());
            path = Paths.get("USER_FILES/" + user.getId(), path.toString());
            var resource = new UrlResource(path.toUri());
            var size = FileUploadUtils.getFolderSize(resource.getFile());
            double gbSize = (double)size/(1024*1024+1024);
            return new ResponseEntity<>(new SpaceUsageResponse(gbSize, MAX_SPACE-gbSize, MAX_SPACE), HttpStatus.OK);
        }
    }
}
