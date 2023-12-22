package com.web.datadropapi.Mappers;

import com.web.datadropapi.Models.DirectoryDto;
import com.web.datadropapi.Models.FileDto;
import com.web.datadropapi.Models.SubdirectoryDto;
import com.web.datadropapi.Repositories.Entities.DirectoryEntity;
import com.web.datadropapi.Repositories.Entities.FileEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class FileMapperService {

    @Autowired
    UserMapperService userMapperService;
    public FileDto mapFileEntityToDto(FileEntity entity){
        var dto = new FileDto();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setParentDirectoryId(entity.getParentDirectory().getId());
        dto.setCreationDate(entity.getCreationDate());
        dto.setSharedState(entity.getSharedState());
        dto.setLastModifiedDate(entity.getLastModifiedDate());
        dto.setMimeType(entity.getMimeType());
        dto.setOwner(userMapperService.mapUserEntityToDto(entity.getOwner()));
        if(entity.getSharedWith() != null)
            dto.setSharedWithUsers(entity.getSharedWith().stream().map(x -> x.getSharedWith().getId()).toList());
        else dto.setSharedWithUsers(new ArrayList<>());
        return dto;
    }

    public SubdirectoryDto mapDirectoryEntityToSubDto(DirectoryEntity entity){
        var dto = new SubdirectoryDto();
        if(entity.getOwner()!=null)
            dto.setOwner(userMapperService.mapUserEntityToDto(entity.getOwner()));
        else dto.setOwner(null);
        if(entity.getParentDirectory() != null)
            dto.setParentDirectoryId(entity.getParentDirectory().getId());
        else dto.setParentDirectoryId(null);
        dto.setName(entity.getName());
        dto.setId(entity.getId());
        dto.setCreationDate(entity.getCreationDate());
        dto.setSharedState(entity.getSharedState());
        dto.setLastModifiedDate(entity.getLastModifiedDate());
        if(entity.getSharedWith()!=null)
            dto.setSharedWithUsers(entity.getSharedWith().stream().map(x -> x.getSharedWith().getId()).toList());
        else dto.setSharedWithUsers(new ArrayList<>());

        return dto;
    }

    public List<SubdirectoryDto> mapDirectoryEntitiesToSubDtos(List<DirectoryEntity> entities){
        var dtos = new ArrayList<SubdirectoryDto>();
        for(var entity : entities){
            dtos.add(mapDirectoryEntityToSubDto(entity));
        }
        return dtos;
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
        if(entity.getFiles() != null)
            dto.setFiles(mapFileEntitiesToDtos(entity.getFiles()));
        else dto.setFiles(new ArrayList<>());
        if(entity.getOwner()!=null)
            dto.setOwner(userMapperService.mapUserEntityToDto(entity.getOwner()));
        else dto.setOwner(null);
        if(entity.getParentDirectory() != null)
            dto.setParentDirectoryId(entity.getParentDirectory().getId());
        else dto.setParentDirectoryId(null);
        if(entity.getSubdirectories() != null)
            dto.setSubdirectories(mapDirectoryEntitiesToSubDtos(entity.getSubdirectories()));
        else dto.setSubdirectories(new ArrayList<>());
        dto.setName(entity.getName());
        dto.setId(entity.getId());
        dto.setCreationDate(entity.getCreationDate());
        dto.setSharedState(entity.getSharedState());
        dto.setLastModifiedDate(entity.getLastModifiedDate());
        if(entity.getSharedWith()!=null)
            dto.setSharedWithUsers(entity.getSharedWith().stream().map(x -> x.getSharedWith().getId()).toList());
        else dto.setSharedWithUsers(new ArrayList<>());

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
