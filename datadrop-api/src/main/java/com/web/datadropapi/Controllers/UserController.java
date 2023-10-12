package com.web.datadropapi.Controllers;

import com.web.datadropapi.Enums.UserRole;
import com.web.datadropapi.Enums.UserState;
import com.web.datadropapi.Mappers.UserMapperService;
import com.web.datadropapi.Models.LoginRequest;
import com.web.datadropapi.Models.RegistrationRequest;
import com.web.datadropapi.Models.UserDto;
import com.web.datadropapi.Repositories.Entities.UserEntity;
import com.web.datadropapi.Repositories.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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

    @DeleteMapping("/delete/{id}") //TODO: only admin can use this
    public ResponseEntity<Void> deleteUserById(@PathVariable("id") Long id){
        if(!userRepository.existsById(id))
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        userRepository.deleteById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
