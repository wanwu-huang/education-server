package org.mentpeak.test.vo;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.mentpeak.test.entity.ReportTeacherGrade;

/**
 * 老师年级报告表视图实体类
 *
 * @author lxp
 * @since 2022-07-12
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "ReportTeacherGradeVO对象", description = "老师年级报告表")
public class ReportTeacherGradeVO extends ReportTeacherGrade {
	private static final long serialVersionUID = 1L;

}
