package com.web.datadropapi.Models.Requests;

import com.web.datadropapi.Enums.SharedState;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UploadRequest {
    private Long uploadDirectoryId;
    private SharedState sharedState;
}
