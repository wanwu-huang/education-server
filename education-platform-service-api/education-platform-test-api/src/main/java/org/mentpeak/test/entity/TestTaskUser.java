package org.mentpeak.test.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.mentpeak.core.mybatisplus.base.BaseEntity;

/**
 * 测评任务用户关联表实体类
 *
 * @author lxp
 * @since 2022-07-12
 */
@Data
@TableName("test_task_user")
@ApiModel(value = "TestTaskUser对象", description = "测评任务用户关联表")
public class TestTaskUser extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("主键id")
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 租户编号
     */
    @ApiModelProperty(value = "租户编号")

    private String tenantCode;
    /**
     * 测评任务ID
     */
    @ApiModelProperty(value = "测评任务ID")
    private Long testTaskId;
    /**
     * 用户ID
     */
    @ApiModelProperty(value = "用户ID")
    private Long userId;
    /**
     * 任务完成状态  0 未完成 1已完成
     */
    @ApiModelProperty(value = "任务完成状态  0 未完成 1已完成")
    private Integer completionStatus;
    /**
     * 是否预警  0 否  1 是
     */
    @ApiModelProperty(value = "是否预警  0 否  1 是")
    private Integer isWarn;
    /**
     * 数据是否有效  0 否  1 是
     */
    @ApiModelProperty(value = "数据是否有效  0 否  1 是")
    private Integer isValid;

    @ApiModelProperty(value = "关注等级0 良好  1级关注  2级关注 3级关注")
    private Integer followLevel;

    @ApiModelProperty(value = "家长他评  0 良好 1 1级")
    private Integer parentComments;

    @ApiModelProperty(value = "教师他评 0 良好  1级  2级 3级")
    private Integer teacherComments;

    @ApiModelProperty(value = "学生自评 0 良好  1级  2级 3级")
    private Integer studentComments;


}
