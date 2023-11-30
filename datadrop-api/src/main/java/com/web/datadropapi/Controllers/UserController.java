package com.web.datadropapi.Controllers;

import com.web.datadropapi.Handler.Exception.DuplicateDataException;
import com.web.datadropapi.Mappers.FileMapperService;
import com.web.datadropapi.Mappers.UserMapperService;
import com.web.datadropapi.Models.DirectoryDto;
import com.web.datadropapi.Models.FileDto;
import com.web.datadropapi.Models.Requests.AccountUpdateRequest;
import com.web.datadropapi.Models.Responses.AccountUpdateResponse;
import com.web.datadropapi.Models.Responses.SpaceUsageResponse;
import com.web.datadropapi.Models.UserDto;
import com.web.datadropapi.Repositories.UserRepository;
import com.web.datadropapi.Services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

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
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserService userService;

    @GetMapping("/{id}") //user
    public ResponseEntity<UserDto> getUserById(@PathVariable("id") Long id){
        var user = userService.getUserById(id);
        return ResponseEntity.ok(userMapperService.mapUserEntityToDto(user));
    }


    @GetMapping("/shared/files")
    public ResponseEntity<List<FileDto>>getFilesSharedWithCurrentUser(){
        var user = userService.getCurrentUser();
        var sharedWith = userService.getFilesSharedWithUser(user);
        return new ResponseEntity<>(fileMapperService.mapFileEntitiesToDtos(sharedWith), HttpStatus.OK);
    }

    @GetMapping("/shared/directories")
    public ResponseEntity<List<DirectoryDto>> getDirectoriesSharedWithUser(){
        var user = userService.getCurrentUser();
        var sharedWith = userService.getDirectoriesSharedWithUser(user);
        return new ResponseEntity<>(fileMapperService.mapDirectoryEntitiesToDtos(sharedWith), HttpStatus.OK);
    }

    @PutMapping("/account")
    public ResponseEntity<AccountUpdateResponse> putAccountDetails(@Valid @RequestBody AccountUpdateRequest request){
        var user = userService.getCurrentUser();
        if(!passwordEncoder.matches(request.getPassword(), user.getPassword()))
            throw new SecurityException("Incorrect password.");

        var opt = userRepository.findByName(request.getUsername());
        if(opt.isPresent() && !opt.get().getId().equals(user.getId())){ //username is already taken
            throw new DuplicateDataException("Username is already taken!");
        }
        user.setName(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setLastModifiedDate(LocalDate.now());
        user.setEmail(request.getEmail());
        userRepository.save(user);
        var resp = new AccountUpdateResponse(user.getId(), user.getEmail(), user.getName());

        return new ResponseEntity<>(resp, HttpStatus.OK);
    }

    @DeleteMapping("/account")
    public ResponseEntity<Void> deleteUserAccountById() throws IOException {
        var user = userService.getCurrentUser();
        if(userService.deleteUserAccount(user))
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        else throw new SecurityException("Could not delete user files.");
    }


    @GetMapping("/storage")
    public ResponseEntity<SpaceUsageResponse> getSpaceUsageStatsOfCurrentUser() throws IOException {
        var user = userService.getCurrentUser();
        return new ResponseEntity<>(userService.getStorageUsageOfUser(user), HttpStatus.OK);
    }
}
