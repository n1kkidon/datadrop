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
}
