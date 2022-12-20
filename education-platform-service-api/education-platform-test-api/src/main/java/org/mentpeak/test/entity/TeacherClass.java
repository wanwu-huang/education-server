package org.mentpeak.test.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.mentpeak.core.mybatisplus.base.BaseEntity;

/**
 * 班主任班级关联表实体类
 *
 * @author lxp
 * @since 2022-07-12
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "TeacherClass对象", description = "班主任班级关联表")
public class TeacherClass extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 租户编号
     */
    @ApiModelProperty(value = "租户编号")
    private String tenantCode;
    /**
     * 教师ID
     */
    @ApiModelProperty(value = "教师ID")
    private Long teacherId;
    /**
     * 班级ID
     */
    @ApiModelProperty(value = "班级ID")
    private Long classId;
    /**
     * 年级ID
     */
    @ApiModelProperty(value = "年级ID")
    private Long gradeId;

}
