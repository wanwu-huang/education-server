package org.mentpeak.test.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.mentpeak.test.entity.ReportTeacherClass;

/**
 * 老师班级报告表数据传输对象实体类
 *
 * @author lxp
 * @since 2022-07-12
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ReportTeacherClassDTO extends ReportTeacherClass {
	private static final long serialVersionUID = 1L;

}
