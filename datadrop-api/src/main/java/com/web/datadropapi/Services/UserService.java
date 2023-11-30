package com.web.datadropapi.Services;

import com.web.datadropapi.Enums.SharedState;
import com.web.datadropapi.Models.Responses.SpaceUsageResponse;
import com.web.datadropapi.Repositories.DirectoryRepository;
import com.web.datadropapi.Repositories.Entities.*;
import com.web.datadropapi.Repositories.UserRepository;
import com.web.datadropapi.Utils.FileUploadUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.UrlResource;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class UserService {
    public final UserRepository userRepository;
    public final DirectoryRepository directoryRepository;
    public UserEntity getCurrentUser(){
        var userId = ((UserEntity)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
        var optUser = userRepository.findById(userId);
        if(optUser.isEmpty())
            throw new NoSuchElementException("the user with the given id doesn't exist.");
        return optUser.get();
    }

    public UserEntity getUserById(long id){
        var user = userRepository.findById(id);
        if(user.isEmpty())
            throw new NoSuchElementException("Requested user was not found.");
        return user.get();
    }

    public List<FileEntity> getFilesSharedWithUser(UserEntity user){
        return user.getSharedFilesWithUser().stream().map(SharedFileEntity::getDirectory).toList();
    }
    public List<DirectoryEntity> getDirectoriesSharedWithUser(UserEntity user){
        return user.getSharedDirectoriesWithUser().stream().map(SharedDirectoryEntity::getDirectory).toList();
    }
    public List<UserEntity> getUsersFileIsSharedWith(FileEntity file){
        if(file.getSharedState().equals(SharedState.PRIVATE)){
            return new ArrayList<>();
        }
        return file.getSharedWith().stream().map(SharedFileEntity::getSharedWith).toList();
    }
    public List<UserEntity> getUsersDirectoryIsSharedWith(DirectoryEntity directory){
        if(directory.getSharedState().equals(SharedState.PRIVATE)){
            return new ArrayList<>();
        }
        return directory.getSharedWith().stream().map(SharedDirectoryEntity::getSharedWith).toList();
    }

    public boolean deleteUserAccount(UserEntity user) throws IOException {
        var rootDir = directoryRepository.findByOwner_idAndParentDirectory_IdIsNull(user.getId());
        if(rootDir != null && !rootDir.isEmpty()){
            var path = Path.of(rootDir.get(0).getAbsolutePath());
            path = Paths.get("USER_FILES/" + user.getId(), path.toString());
            var resource = new UrlResource(path.toUri());
            var success = FileUploadUtils.deleteFolder(resource.getFile());
            if(success){
                userRepository.delete(user);
                return true;
            }
            else return false;
        }
        userRepository.delete(user);
        return true;
    }

    public SpaceUsageResponse getStorageUsageOfUser(UserEntity user) throws IOException {
        double MAX_SPACE = 20;

        var rootDir = directoryRepository.findByOwner_idAndParentDirectory_IdIsNull(user.getId());
        if(rootDir == null || rootDir.isEmpty()){
            return new SpaceUsageResponse(0, MAX_SPACE, MAX_SPACE);
        }
        else {
            var path = Path.of(rootDir.get(0).getAbsolutePath());
            path = Paths.get("USER_FILES/" + user.getId(), path.toString());
            var resource = new UrlResource(path.toUri());
            var size = FileUploadUtils.getFolderSize(resource.getFile());
            double gbSize = (double)size/(1024*1024+1024);
            return new SpaceUsageResponse(gbSize, MAX_SPACE-gbSize, MAX_SPACE);
        }
    }

    public DirectoryEntity getCurrentUserRootDirectory(){
        var user = getCurrentUser();
        var root = directoryRepository.findByOwner_idAndParentDirectory_IdIsNull(user.getId());
        return root.getFirst();
    }

}
