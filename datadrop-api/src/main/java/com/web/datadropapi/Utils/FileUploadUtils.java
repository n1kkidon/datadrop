package com.web.datadropapi.Utils;

import com.web.datadropapi.Repositories.Entities.DirectoryEntity;
import org.springframework.web.multipart.MultipartFile;

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
}
