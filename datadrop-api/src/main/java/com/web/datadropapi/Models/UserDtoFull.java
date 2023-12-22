package com.web.datadropapi.Models;

import com.web.datadropapi.Models.Responses.SpaceUsageResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDtoFull extends UserDto{
    private SpaceUsageResponse usage;
}
