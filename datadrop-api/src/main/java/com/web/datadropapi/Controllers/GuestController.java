package com.web.datadropapi.Controllers;

import com.web.datadropapi.Config.JwtService;
import com.web.datadropapi.Enums.SharedState;
import com.web.datadropapi.Enums.UserRole;
import com.web.datadropapi.Enums.UserState;
import com.web.datadropapi.Models.Requests.LoginRequest;
import com.web.datadropapi.Models.Requests.RegistrationRequest;
import com.web.datadropapi.Models.Responses.AuthenticationResponse;
import com.web.datadropapi.Repositories.DirectoryRepository;
import com.web.datadropapi.Repositories.Entities.DirectoryEntity;
import com.web.datadropapi.Repositories.Entities.UserEntity;
import com.web.datadropapi.Repositories.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@RequestMapping("/guest")
@Validated
public class GuestController {
    public final DirectoryRepository directoryRepository;
    public final JwtService jwtService;
    public final AuthenticationManager authenticationManager;
    public final UserRepository userRepository;
    public final PasswordEncoder passwordEncoder;
    @PostMapping("/register") //unauthorized
    public ResponseEntity<Void> registerNewUser(@Valid @RequestBody RegistrationRequest request){
        var opt = userRepository.findByName(request.getUsername());
        if(opt.isPresent())
            return new ResponseEntity<>(HttpStatus.CONFLICT);

        var user = new UserEntity();
        user.setName(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setCreationDate(LocalDate.now());
        user.setLastModifiedDate(LocalDate.now());
        user.setRole(UserRole.ROLE_USER);
        user.setEmail(request.getEmail());
        user.setState(UserState.ACTIVE);
        user = userRepository.save(user); //user created, now create root for user

        var directory = new DirectoryEntity();
        directory.setCreationDate(LocalDate.now());
        directory.setOwner(user);
        directory.setSharedState(SharedState.PRIVATE);
        directory.setLastModifiedDate(LocalDate.now());
        directoryRepository.save(directory);

        return new ResponseEntity<>(HttpStatus.CREATED);
    }
    @PostMapping("/login") //unauthorized
    public ResponseEntity<AuthenticationResponse> login(@Valid @RequestBody LoginRequest request){
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        var opt = userRepository.findByName(request.getUsername());
        if(opt.isEmpty())
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        var user = opt.get();
        var jwtToken = jwtService.generateToken(user);
        return ResponseEntity.ok(new AuthenticationResponse(jwtToken));
    }
}
