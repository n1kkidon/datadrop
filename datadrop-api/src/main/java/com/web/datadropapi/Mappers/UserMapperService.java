package com.web.datadropapi.Mappers;

import com.web.datadropapi.Models.DirectoryDto;
import com.web.datadropapi.Models.UserDto;
import com.web.datadropapi.Repositories.Entities.DirectoryEntity;
import com.web.datadropapi.Repositories.Entities.FileEntity;
import com.web.datadropapi.Repositories.Entities.UserEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserMapperService {
    public UserDto mapUserEntityToDto(UserEntity userEntity) {
        UserDto userDto = new UserDto();
        userDto.setId(userEntity.getId());
        userDto.setName(userEntity.getName());
        userDto.setCreationDate(userEntity.getCreationDate());
        userDto.setEmail(userEntity.getEmail());
        userDto.setState(userEntity.getState());
        return userDto;
    }

    public List<UserDto> mapUserEntitiesToDtos(List<UserEntity> userEntities) {
        List<UserDto> userDtos = new ArrayList<>();
        for (UserEntity userEntity : userEntities) {
            userDtos.add(mapUserEntityToDto(userEntity));
        }
        return userDtos;
    }

    public DirectoryDto mapDirectoryEntityToDto(DirectoryEntity entity){
        var dto = new DirectoryDto();
        dto.setLastModifiedDate(entity.getLastModifiedDate());
        dto.setSharedState(entity.getSharedState());
        dto.setName(entity.getName());
        dto.setId(entity.getId());
        dto.setCreationDate(entity.getCreationDate());
        dto.setFiles(entity.getFiles().stream().map(FileEntity::getId).toList());
        dto.setParentDirectoryId(entity.getParentDirectory().getId());
        dto.setOwnerId(entity.getOwner().getId());
        dto.setSubdirectories(entity.getSubdirectories().stream().map(DirectoryEntity::getId).toList());
        return dto;
    }
}
