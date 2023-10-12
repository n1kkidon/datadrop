package com.web.datadropapi.Models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NameUpdateRequest {
    private String newName;
    private Long itemId;
}
