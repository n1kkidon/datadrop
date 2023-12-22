package com.web.datadropapi.Services;

import com.web.datadropapi.Enums.SharedState;
import com.web.datadropapi.Enums.UserRole;
import com.web.datadropapi.Handler.Exception.DuplicateDataException;
import com.web.datadropapi.Repositories.DirectoryRepository;
import com.web.datadropapi.Repositories.Entities.DirectoryEntity;
import com.web.datadropapi.Repositories.Entities.SharedDirectoryEntity;
import com.web.datadropapi.Repositories.SharedDirectoryRepository;
import com.web.datadropapi.Utils.FileUploadUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class DirectoryService {

    public final DirectoryRepository directoryRepository;
    public final UserService userService;
    public final SharedDirectoryRepository sharedDirectoryRepository;

    public DirectoryEntity getDirectoryById(Long id){
        var directory = directoryRepository.findById(id);
        if(directory.isEmpty())
            throw new NoSuchElementException("Requested destination directory not found.");
        return directory.get();
    }

    public DirectoryEntity renameDirectory(DirectoryEntity dir, String newName) throws IOException {
        var directory = dir.getParentDirectory();
        var resource = directory.getChildItemInSystem(dir.getName());
        if(dir.getName().equals(newName))
            return directory;

        if(resource.exists() && resource.getFile().isDirectory())
        {
            if(dir.getParentDirectory().containsItem(newName)){
                throw new DuplicateDataException(String.format("filename %s already exists in %s", newName, dir.getParentDirectory().getAbsolutePath()));
            }
            if(resource.getFile().renameTo(new File(resource.getFile().getParentFile().getPath()+ "/" + newName))) {
                dir.setName(newName);
                dir.setLastModifiedDate(LocalDate.now());
                return directoryRepository.save(dir);
            }
            else throw new SecurityException("Could not rename the requested file.");

        }
        else throw new NoSuchElementException("Could not find the directory in the server");
    }

    public void deleteDirectory(DirectoryEntity directory) throws IOException {
        var resource = directory.getParentDirectory().getChildItemInSystem(directory.getName());
        if(resource.exists()){
            System.out.println(resource.getFile().isDirectory());
            if(resource.getFile().isDirectory() && FileUploadUtils.deleteFolder(resource.getFile())){
                directoryRepository.delete(directory);
                return; //new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            else throw new SecurityException("Could not access the directory in the server"); //not sufficient permissions in the server OS
        }
        else throw new NoSuchElementException("Could not find the directory in the server");
    }

    public DirectoryEntity getUserAccessibleDirectory(Long id){
        var user = userService.getCurrentUser();
        var directory = getDirectoryById(id);

        if(directory.getOwner().getId().equals(user.getId())){ //directory is owned by current user
            return directory;
        }
        else if(user.getRole().equals(UserRole.ROLE_ADMIN)) { //current user is admin
            return directory;
        }
        else if(directory.getSharedState().equals(SharedState.PUBLIC)) { //directory is public to everyone
            return directory;
        }
        else if(userService.getUsersDirectoryIsSharedWith(directory).stream().anyMatch(x -> x.getId().equals(user.getId()))){ //directory is shared with current user
            return directory;
        }
        throw new SecurityException("forbidden");
    }

    public void setSharedWithByIds(DirectoryEntity directory, List<Long> userIds){
        var sharedWith = directory.getSharedWith();
        for(var userId : userIds) {
            var user = userService.userRepository.findById(userId);

            if (user.isEmpty())
                throw new NoSuchElementException(String.format("Requested user with id %s was not found.", userId));


            if (sharedWith!= null && sharedWith.stream().anyMatch(x -> x.getSharedWith().getId().equals(userId))){
                continue;
                //throw new DuplicateDataException(String.format("You are already sharing this directory with userId %s", userId));
            }

            var temp = new SharedDirectoryEntity();
            temp.setDirectory(directory);
            temp.setSharedWith(user.get());
            sharedDirectoryRepository.save(temp);
        }
    }

    public void removeSharingWithUsersByIds(DirectoryEntity directory, List<Long> userIds){
        var sharedWith = directory.getSharedWith();
        for(var singleShare: sharedWith){
            if (userIds.stream().anyMatch(x -> x.equals(singleShare.getSharedWith().getId()))){
                sharedDirectoryRepository.delete(singleShare);
            }
        }
    }



}
