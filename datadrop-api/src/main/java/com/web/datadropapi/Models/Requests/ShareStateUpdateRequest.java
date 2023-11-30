package com.web.datadropapi.Models.Requests;

import com.web.datadropapi.Enums.SharedState;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShareStateUpdateRequest {
    private Long itemId;
    private SharedState state;
    private List<Long> userIds;
}
