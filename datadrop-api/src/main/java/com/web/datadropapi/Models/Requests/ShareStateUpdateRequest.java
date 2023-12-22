package com.web.datadropapi.Models.Requests;

import com.web.datadropapi.Enums.SharedState;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShareStateUpdateRequest {
    @NotNull
    private Long itemId;
    @NotNull
    private SharedState state;
    private List<Long> shareWithUserIds;
    private List<Long> stopSharingWithUserIds;
}

