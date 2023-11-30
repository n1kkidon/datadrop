package com.web.datadropapi.Controllers;

import com.web.datadropapi.Enums.UserState;
import com.web.datadropapi.Mappers.FileMapperService;
import com.web.datadropapi.Mappers.UserMapperService;
import com.web.datadropapi.Models.DirectoryDto;
import com.web.datadropapi.Models.FileDto;
import com.web.datadropapi.Models.Responses.SpaceUsageResponse;
import com.web.datadropapi.Models.UserDto;
import com.web.datadropapi.Repositories.UserRepository;
import com.web.datadropapi.Services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
@Validated
public class AdminController {

    public final UserService userService;
    public final UserRepository userRepository;
    public final UserMapperService userMapperService;
    public final FileMapperService fileMapperService;
    @PatchMapping("/block/{id}") //admin
    public ResponseEntity<UserDto> changeBlockStateOfUserAccountById(@PathVariable("id") Long id){
        var user = userService.getUserById(id);
        if(user.getState() == UserState.ACTIVE)
            user.setState(UserState.BANNED);
        else user.setState(UserState.ACTIVE);
        userRepository.save(user);
        return new ResponseEntity<>(userMapperService.mapUserEntityToDto(user), HttpStatus.OK);
    }

    @GetMapping("/storage/{id}") //admin
    public ResponseEntity<SpaceUsageResponse> getSpaceUsageStatsByUserId(@PathVariable("id") Long id) throws IOException {
        var user = userService.getUserById(id);
        return new ResponseEntity<>(userService.getStorageUsageOfUser(user), HttpStatus.OK);
    }

    @GetMapping("/all") //admin
    public ResponseEntity<List<UserDto>> getAllUsers(){
        var users = userRepository.findAll();
        var usersDto = userMapperService.mapUserEntitiesToDtos(users);
        return ResponseEntity.ok(usersDto);
    }

    @GetMapping("/shared/files/{id}") //admin
    public ResponseEntity<List<FileDto>> getFilesSharedWithUser(@PathVariable("id") Long id){
        var user = userService.getUserById(id);
        var sharedWith = userService.getFilesSharedWithUser(user);
        return new ResponseEntity<>(fileMapperService.mapFileEntitiesToDtos(sharedWith), HttpStatus.OK);
    }

    @GetMapping("/shared/directories/{id}") //admin
    public ResponseEntity<List<DirectoryDto>> getDirectoriesSharedWithUser(@PathVariable("id") Long id){
        var user = userService.getUserById(id);
        var sharedWith = userService.getDirectoriesSharedWithUser(user);
        return new ResponseEntity<>(fileMapperService.mapDirectoryEntitiesToDtos(sharedWith), HttpStatus.OK);
    }

    @DeleteMapping("/account/{id}") //admin
    public ResponseEntity<Void> deleteUserAccountById(@PathVariable("id") Long id) throws IOException {
        var user = userService.getUserById(id);
        if(userService.deleteUserAccount(user))
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        else throw new SecurityException("Could not delete user files.");
    }
}
