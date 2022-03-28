package com.swivel.cc.base.domain.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class BasicUserResponseDto extends ResponseDto {

    private String id;
    private String name;
    private String imageUrl;


}
