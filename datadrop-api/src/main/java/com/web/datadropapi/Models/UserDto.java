package com.web.datadropapi.Models;

import com.web.datadropapi.Enums.UserState;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

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

