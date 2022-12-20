package org.mentpeak.test.vo;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.mentpeak.test.entity.ReportTeacherClass;

/**
 * 老师班级报告表视图实体类
 *
 * @author lxp
 * @since 2022-07-12
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "ReportTeacherClassVO对象", description = "老师班级报告表")
public class ReportTeacherClassVO extends ReportTeacherClass {
	private static final long serialVersionUID = 1L;

}
