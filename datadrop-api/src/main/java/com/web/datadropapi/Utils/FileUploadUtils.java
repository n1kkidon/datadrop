package com.web.datadropapi.Utils;

import com.web.datadropapi.Repositories.Entities.DirectoryEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;

public class FileUploadUtils {
    public static void saveFileToSystem(DirectoryEntity location, MultipartFile file) throws IOException {
        var usersFileRoot = "USER_FILES/" + location.getOwner().getId();
        Path path = Paths.get(usersFileRoot, location.getAbsolutePath());
        if(!Files.exists(path))
            Files.createDirectories(path);

        try (InputStream inputStream = file.getInputStream()){
            Path filePath = path.resolve(Objects.requireNonNull(file.getOriginalFilename()));
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
        }
        catch (IOException e){
            throw new IOException("Cannot save file: " + file.getOriginalFilename());
        }
    }
    public static void createFolderIfDoesntExist(DirectoryEntity location) throws IOException {
        var usersFileRoot = "USER_FILES/" + location.getOwner().getId();
        Path path = Paths.get(usersFileRoot, location.getAbsolutePath());
        if(!Files.exists(path))
            Files.createDirectories(path);
    }

    public static boolean deleteFolder(File folder) {
        File[] files = folder.listFiles();

        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    // Recursively delete subfolders and their contents
                    deleteFolder(file);
                } else {
                    // Delete files in the folder
                    file.delete();
                }
            }
        }

        // Delete the empty folder itself
        return folder.delete();
    }

    public static long getFolderSize(File folder) {
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
