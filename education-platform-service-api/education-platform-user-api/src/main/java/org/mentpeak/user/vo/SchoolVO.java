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
@ApiModel ( value = "SchoolVO对象", description = "SchoolVO对象" )
public class SchoolVO implements Serializable {
    private static final long serialVersionUID = 1L;
    @ApiModelProperty ( "用户id" )
    private Integer userId;
    @ApiModelProperty ( "辅导员id" )
    private Long teacherId;
    @ApiModelProperty ( "学校单位名称" )
    private String schoolName;
    @ApiModelProperty ( "辅导员姓名" )
    private String teacherName;
    @ApiModelProperty ( "所在地市id" )
    private Integer addressId;
    @ApiModelProperty ( "所在地省市" )
    private String[] areaIds;
    @ApiModelProperty ( value = "职业ID" )
    private Long jobName;

}
