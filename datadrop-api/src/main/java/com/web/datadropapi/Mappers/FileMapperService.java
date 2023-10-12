package com.web.datadropapi.Mappers;

import com.web.datadropapi.Models.DirectoryDto;
import com.web.datadropapi.Models.FileDto;
import com.web.datadropapi.Repositories.Entities.DirectoryEntity;
import com.web.datadropapi.Repositories.Entities.FileEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class FileMapperService {
    public FileDto mapFileEntityToDto(FileEntity entity){
        var dto = new FileDto();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setCreationDate(entity.getCreationDate());
        dto.setSharedState(entity.getSharedState());
        dto.setLastModifiedDate(entity.getLastModifiedDate());
        return dto;
    }

    public List<FileDto> mapFileEntitiesToDtos(List<FileEntity> entities){
        var dtos = new ArrayList<FileDto>();
        for(var entity : entities){
            dtos.add(mapFileEntityToDto(entity));
        }
        return dtos;
    }

    public DirectoryDto mapDirectoryEntityToDto(DirectoryEntity entity){
        var dto = new DirectoryDto();
        dto.setFiles(entity.getFiles().stream().map(FileEntity::getId).toList());
        dto.setOwnerId(entity.getOwner().getId());
        dto.setParentDirectoryId(entity.getParentDirectory().getId());
        dto.setSubdirectories(entity.getSubdirectories().stream().map(DirectoryEntity::getId).toList());
        dto.setName(entity.getName());
        dto.setId(entity.getId());
        dto.setCreationDate(entity.getCreationDate());
        dto.setSharedState(entity.getSharedState());
        dto.setLastModifiedDate(entity.getLastModifiedDate());
        return dto;
    }

    public List<DirectoryDto> mapDirectoryEntitiesToDtos(List<DirectoryEntity> entities){
        var dtos = new ArrayList<DirectoryDto>();
        for(var entity : entities){
            dtos.add(mapDirectoryEntityToDto(entity));
        }
        return dtos;
    }
}
