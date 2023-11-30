package com.web.datadropapi.Models.Responses;

import com.web.datadropapi.Enums.UserRole;
import lombok.Data;

@Data
public class Authority {
    private UserRole authority;
}
