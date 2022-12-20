package org.mentpeak.test.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author hzl
 * @data 2022年08月27日15:59
 * 年级关注等级分布人数
 */
@Data
public class GFollowVO implements Serializable {

    @ApiModelProperty(value = "年级id")
    private Long taskId;
    @ApiModelProperty(value = "年级id")
    private Long gradeId;
    @ApiModelProperty(value = "班级id")
    private Long classId;
    @ApiModelProperty(value = "参与班级")
    private String className;
    @ApiModelProperty(value = "三级人数-占比")
    private String rateThree;
    @ApiModelProperty(value = "二级人数-占比")
    private String rateTwo;
    @ApiModelProperty(value = "一级人数-占比")
    private String rateOne;
    @ApiModelProperty(value = "良好人数-占比")
    private String rate;

}
