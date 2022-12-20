package org.mentpeak.test.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 测评任务部门关联表实体类
 *
 * @author lxp
 * @since 2022-07-12
 */
@Data
@ApiModel(value = "TestTaskDepartment对象", description = "测评任务部门关联表")
public class TestTaskDepartment implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("主键id")
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 测评任务ID
     */
    @ApiModelProperty(value = "测评任务ID")
    private Long testTaskId;
    /**
     * 测评任务部门ID （年级ID）
     */
    @ApiModelProperty(value = "测评任务部门ID （年级ID）")
    private Long testDepartmentId;


}
