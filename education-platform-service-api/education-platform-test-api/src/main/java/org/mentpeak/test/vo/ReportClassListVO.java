package org.mentpeak.test.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author hzl
 * @data 2022年09月02日17:37
 * 报告列表
 */
@Data
public class ReportClassListVO implements Serializable {

    @ApiModelProperty(value = "部门id")
    private Long gradeId;
    @ApiModelProperty(value = "部门名称")
    private String gradeName;
    @ApiModelProperty(value = "班级id")
    private Long classId;
    @ApiModelProperty(value = "班级名称")
    private String className;
    @ApiModelProperty(value = "参加测评人数")
    private Integer totalPeople = 0;
    @ApiModelProperty(value = "测评完成人数")
    private Integer testPeople = 0;
    @ApiModelProperty(value = "任务id")
    private Long taskId;
    @ApiModelProperty(value = "是否有权限 0没有 1有 ")
    private Integer isPermission = 1;
    @ApiModelProperty(value = "是否有报告 0没有 1有 ")
    private Integer isReport = 0;
}
