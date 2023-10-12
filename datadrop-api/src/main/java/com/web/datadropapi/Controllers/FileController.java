package com.web.datadropapi.Controllers;

import com.web.datadropapi.Enums.SharedState;
import com.web.datadropapi.Models.DirectoryDto;
import com.web.datadropapi.Models.UploadRequest;
import com.web.datadropapi.Repositories.DirectoryRepository;
import com.web.datadropapi.Repositories.Entities.DirectoryEntity;
import com.web.datadropapi.Repositories.Entities.FileEntity;
import com.web.datadropapi.Repositories.FileRepository;
import com.web.datadropapi.Repositories.UserRepository;
import com.web.datadropapi.Utils.FileUploadUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
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

    @PostMapping("/post/upload")
    public ResponseEntity<String> uploadFile(@Param("file") MultipartFile file, UploadRequest request) throws IOException {
        if(file == null)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        var entity = new FileEntity();
        entity.setCreationDate(LocalDate.now());
        entity.setName(file.getOriginalFilename());
        entity.setSharedState(SharedState.PRIVATE);

        DirectoryEntity directory;
        if(request.getUploadDirectoryId() == null) {
            directory = new DirectoryEntity();
            directory.setCreationDate(LocalDate.now());
            //directory.set
        }
        else {
            var optionalDirectory = directoryRepository.findById(request.getUploadDirectoryId());
            if (optionalDirectory.isEmpty())
                return new ResponseEntity<>("Requested destination directory not found.", HttpStatus.NOT_FOUND);
            else directory = optionalDirectory.get();
        }
        entity.setParentDirectory(directory);
        entity.setLastModifiedDate(LocalDate.now());
        fileRepository.save(entity);
        FileUploadUtils.saveFileToSystem(directory, file);

        return new ResponseEntity<>(HttpStatus.OK);
    }
    @PostMapping("/post/createdir") //TODO: move this to DirectoryController with /directory route
    public ResponseEntity<String> createDirectory(@RequestBody DirectoryDto request){
        var entity = new DirectoryEntity();
        entity.setCreationDate(request.getCreationDate()); //TODO: serverside
        entity.setLastModifiedDate(request.getLastModifiedDate());

        if(request.getParentDirectoryId() != null){
            var directory = directoryRepository.findById(request.getParentDirectoryId());
            if(directory.isEmpty())
                return new ResponseEntity<>("Requested destination directory not found.", HttpStatus.NOT_FOUND);
            var parent = directory.get();
            if(parent.getFiles().stream().anyMatch(x -> Objects.equals(x.getName(), request.getName()))){
                return new ResponseEntity<>(String.format("Requested destination directory already has a file named '%s'.",
                                request.getName()), HttpStatus.CONFLICT);
            }

            entity.setParentDirectory(parent);
        }

        entity.setName(request.getName());
        entity.setOwner(userRepository.findById(request.getOwnerId()).get()); //TODO: this should be fixed to get a user from jwt token
        entity.setSharedState(request.getSharedState());
        directoryRepository.save(entity);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/get/debugDir/{id}")
    public ResponseEntity<DirectoryEntity> testDirEntity(@PathVariable("id") Long id){
        var dir = directoryRepository.findById(id);
        return new ResponseEntity<>(dir.get(), HttpStatus.OK);
    }
}
