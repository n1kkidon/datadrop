package com.web.datadropapi.Services;

import com.web.datadropapi.Enums.SharedState;
import com.web.datadropapi.Enums.UserRole;
import com.web.datadropapi.Handler.Exception.DuplicateDataException;
import com.web.datadropapi.Mappers.FileMapperService;
import com.web.datadropapi.Repositories.Entities.FileEntity;
import com.web.datadropapi.Repositories.FileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class FileService {

    public final FileRepository fileRepository;
    public final UserService userService;
    public final FileMapperService fileMapperService;

    public FileEntity getFileById(Long id){
        var opt = fileRepository.findById(id);
        if(opt.isEmpty())
            throw new NoSuchElementException("Requested file was not found.");
        return opt.get();
    }

    public FileEntity getUserAccessibleFileById(Long id){
        var user = userService.getCurrentUser();
        var file = getFileById(id);
        var directory = file.getParentDirectory();
        if(directory.getOwner().getId().equals(user.getId())){ //file is owned by current user
            return file;
        }
        else if(user.getRole().equals(UserRole.ROLE_ADMIN)){ //current user is admin
            return file;
        }
        else if(userService.getUsersFileIsSharedWith(file).stream().anyMatch(x -> x.getId().equals(user.getId()))){ //file is shared with current user
            return file;
        }
        else if(file.getSharedState().equals(SharedState.PUBLIC)){ //file is public to everyone
            return file;
        }
        throw new SecurityException("forbidden");
    }


    public FileEntity renameFile(FileEntity file, String newName) throws IOException {

        var resource = file.getParentDirectory().getChildItemInSystem(file.getName());
        if(resource.exists() && resource.isReadable())
        {
            if(file.getParentDirectory().containsItem(newName)){
                throw new DuplicateDataException(String.format("filename %s already exists in %s", newName, file.getParentDirectory().getAbsolutePath()));
            }
            if(resource.getFile().renameTo(new File(resource.getFile().getParentFile().getPath()+ "/" + newName))) {
                file.setName(newName);
                return fileRepository.save(file);
            }
            else throw new SecurityException("Could not rename the requested file.");

        }
        else throw new NoSuchElementException("Could not find the file in the server");
    }

    public void deleteFile(FileEntity file) throws IOException {
        var resource = file.getParentDirectory().getChildItemInSystem(file.getName());
        if(!file.getParentDirectory().getOwner().getId().equals(userService.getCurrentUser().getId())
                && !userService.getCurrentUser().getRole().equals(UserRole.ROLE_ADMIN)) {
            //can delete files if you own them, or you are admin
            throw new SecurityException("forbidden");
        }
        if(resource.exists()){
            if(resource.isReadable() && resource.getFile().delete()){
                fileRepository.delete(file);
            }
            else throw new SecurityException("Could not access the file in the server"); //not sufficient permissions in the server OS
        }
        else throw new NoSuchElementException("Could not find the file in the server");
    }

    public Resource getFile(FileEntity file) throws MalformedURLException {
        var resource = file.getParentDirectory().getChildItemInSystem(file.getName());
        if(resource.exists()){
            if(resource.isReadable()){
                return resource;
            }
            else throw new SecurityException("Could not access the file in the server");
        }
        else throw new NoSuchElementException("Could not find the file in the server");
    }
}
