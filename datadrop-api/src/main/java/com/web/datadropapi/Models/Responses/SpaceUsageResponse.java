package com.web.datadropapi.Models.Responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SpaceUsageResponse {
    private double spaceUsedGb;
    private double spaceAvailableGb;
    private double totalSpaceGb;
}
