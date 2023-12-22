package com.web.datadropapi.Models;

import com.web.datadropapi.Enums.UserState;
import com.web.datadropapi.Models.Responses.SpaceUsageResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    private Long id;
    private String name;
    private LocalDate creationDate;
    private String email;
    private UserState state;
}

