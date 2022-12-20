package org.mentpeak.test.vo;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 任务管理信息
 * @author: demain_lee
 * @since:  2022-07-14
 */
@Data
public class TaskDetailVO implements Serializable {


    /**
     * 任务ID
     */
    @ApiModelProperty(value = "任务ID")
    @ExcelIgnore
    private Long testTaskId;

    /**
     * 用户ID
     */
    @ApiModelProperty(value = "用户ID")
    @ExcelProperty( "用户ID" )
    private Long userId;

    /**
     * 姓名
     */
    @ApiModelProperty(value = "姓名")
    @ExcelProperty( "姓名" )
    private String realName;

    /**
     * 学籍号
     */
    @ApiModelProperty(value = "学籍号")
    @ExcelProperty ( "学籍号" )
    private String account;

    /**
     * 性别
     */
    @ApiModelProperty(value = "性别")
    @ExcelIgnore
    private Integer sex;

    /**
     * 性别名字
     */
    @ApiModelProperty(value = "性别名字")
    @ExcelProperty ( "性别" )
    private String sexName;

    /**
     * 一级部门
     */
    @ApiModelProperty(value = "一级部门")
    @ExcelIgnore
    private Long testDepartmentId;

    /**
     * 一级部门名字
     */
    @ApiModelProperty(value = "一级部门名字")
    @ExcelProperty ( "一级部门" )
    private String gradeName;

    /**
     * 二级部门
     */
    @ApiModelProperty(value = "二级部门")
    @ExcelIgnore
    private Long classId;


    /**
     * 二级部门名字
     */
    @ApiModelProperty(value = "二级部门名字")
    @ExcelProperty ( "二级部门" )
    private String className;

    /**
     * 完成情况
     */
    @ApiModelProperty(value = "完成情况 0 未完成 1已完成")
    @ExcelIgnore
    private Integer completionStatus;

    /**
     * 测评时间
     */
    @ApiModelProperty(value = "测评时间")
    @ExcelProperty ( "测评时间" )
    private String startTime;
}
