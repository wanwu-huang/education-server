package org.mentpeak.user.vo;


import java.io.Serializable;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 视图实体类
 *
 * @author mp
 */
@Data
@ApiModel ( value = "UserSchoolVO对象", description = "UserSchoolVO对象" )
public class UserSchoolVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty ( "学校单位名称" )
    private String schoolName;

    @ApiModelProperty ( "所在地省市" )
    private String[] address;

    @ApiModelProperty ( value = "职业ID" )
    private Long jobId;
}
