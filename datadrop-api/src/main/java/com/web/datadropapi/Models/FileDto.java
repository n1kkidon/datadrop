package com.web.datadropapi.Models;

import com.web.datadropapi.Enums.SharedState;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileDto {
    private Long id;
    private String name;
    private LocalDate creationDate;
    private LocalDate lastModifiedDate;
    private SharedState sharedState;
    private List<Long> sharedWithUsers;
}
