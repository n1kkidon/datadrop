package com.web.datadropapi.Utils;

import com.web.datadropapi.Repositories.Entities.DirectoryEntity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;

@Service
public class FileSystemUtils {

    @Value("${user-files.root}")
    private String USER_FILES;

    public Resource getChildItemInSystem(DirectoryEntity directory, String name) throws MalformedURLException {
        var path = Path.of(directory.getAbsolutePath()).resolve(name);
        path = Paths.get(USER_FILES + directory.getOwner().getId(), path.toString());
        return new UrlResource(path.toUri());
    }

    public Path saveFileToSystem(DirectoryEntity location, MultipartFile file) throws IOException {
        var usersFileRoot = USER_FILES + location.getOwner().getId();
        Path path = Paths.get(usersFileRoot, location.getAbsolutePath());
        if(!Files.exists(path))
            Files.createDirectories(path);

        try (InputStream inputStream = file.getInputStream()){
            Path filePath = path.resolve(Objects.requireNonNull(file.getOriginalFilename()));
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
            return filePath;
        }
        catch (IOException e){
            throw new IOException("Cannot save file: " + file.getOriginalFilename());
        }
    }
    public void createFolderIfDoesntExist(DirectoryEntity location) throws IOException {
        var usersFileRoot = USER_FILES + location.getOwner().getId();
        Path path = Paths.get(usersFileRoot, location.getAbsolutePath());
        if(!Files.exists(path))
            Files.createDirectories(path);
    }

    public boolean deleteFolder(File folder) {
        File[] files = folder.listFiles();

        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    // Recursively delete subfolders and their contents
                    deleteFolder(file);
                } else {
                    // Delete files in the folder
                    file.delete(); //TODO: partially successful?
                }
            }
        }

        // Delete the empty folder itself
        return folder.delete();
    }

    public long getFolderSize(File folder) {
        if (!folder.exists()) {
            return -1; // Folder doesn't exist
        }

        if (folder.isFile()) {
            return folder.length();
        }

        long size = 0;
        File[] files = folder.listFiles();

        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    size += getFolderSize(file); // Recursive call for subfolders
                } else {
                    size += file.length();
                }
            }
        }

        return size;
    }
}
