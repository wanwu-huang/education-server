package org.mentpeak.test.vo;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.mentpeak.test.entity.TeacherClass;

/**
 * 班主任班级关联表视图实体类
 *
 * @author lxp
 * @since 2022-07-12
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "TeacherClassVO对象", description = "班主任班级关联表")
public class TeacherClassVO extends TeacherClass {
	private static final long serialVersionUID = 1L;

}
