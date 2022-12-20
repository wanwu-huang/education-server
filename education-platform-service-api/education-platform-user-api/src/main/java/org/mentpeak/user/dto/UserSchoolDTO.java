package org.mentpeak.user.dto;


import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


/**
 * 视图实体类
 *
 * @author mp
 */
@Data
public class UserSchoolDTO {

    @ApiModelProperty ( "学校单位名称" )
    private String schoolName;

    @ApiModelProperty ( value = "职业ID" )
    private Long jobId;

    @ApiModelProperty ( "所在地市id" )
    private Integer addressId;
}
