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
@ApiModel ( value = "TeacherVO对象", description = "TeacherVO对象" )
public class TeacherVO implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty ( "主键id" )
    private Integer id;

    @ApiModelProperty ( "姓名" )
    private String realName;
}
