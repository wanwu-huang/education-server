package org.mentpeak.test.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.mentpeak.core.mybatisplus.base.BaseEntity;

/**
 * 老师年级报告表实体类
 *
 * @author lxp
 * @since 2022-07-12
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "ReportTeacherGrade对象", description = "老师年级报告表")
public class ReportTeacherGrade extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("主键id")
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    /**
     * 租户编号
     */
    @ApiModelProperty(value = "租户编号")
    private String tenantCode;
    @ApiModelProperty(value = "教师ID")
    private Long teacherId;
    /**
     * 年级ID
     */
    @ApiModelProperty(value = "年级ID")
    private Long gradeId;

    /**
     * 任务ID
     */
    @ApiModelProperty(value = "任务ID")
    private Long taskId;
    /**
     * 角色名称
     */
    @ApiModelProperty(value = "角色名称")
    private String roleName;


}
