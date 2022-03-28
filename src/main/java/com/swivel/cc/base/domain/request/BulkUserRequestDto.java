package com.swivel.cc.base.domain.request;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Bulk user request Dto to get list of users from auth service
 */
@Getter
@Setter
@AllArgsConstructor
public class BulkUserRequestDto extends RequestDto {

    private List<String> userIds;

    /**
     * This method checks all required fields are available.
     *
     * @return true/ false
     */
    @Override
    public boolean isRequiredAvailable() {
        return userIds != null && !userIds.isEmpty();
    }

    /**
     * This method converts this object to json string for logging purpose.
     * All fields are obfuscated.
     *
     * @return json string
     */
    @Override
    public String toLogJson() {
        return toJson();
    }
}
