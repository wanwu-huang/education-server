package org.mentpeak.user.dto;

import javax.validation.constraints.NotBlank;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ResetPasswordDTO {

    @ApiModelProperty ( "userId集合" )
    @NotBlank ( message = "userId集合不能为空" )
    private String userIds;
}
