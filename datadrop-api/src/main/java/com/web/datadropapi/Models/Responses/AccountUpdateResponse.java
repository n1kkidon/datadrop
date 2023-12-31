package com.web.datadropapi.Models.Responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountUpdateResponse {
    private Long id;
    private String email;
    private String username;
}
