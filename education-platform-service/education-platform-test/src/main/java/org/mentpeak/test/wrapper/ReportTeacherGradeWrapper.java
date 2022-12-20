package org.mentpeak.test.wrapper;

import lombok.AllArgsConstructor;
import org.mentpeak.core.mybatisplus.support.BaseEntityWrapper;
import org.mentpeak.core.tool.utils.BeanUtil;
import org.mentpeak.test.entity.ReportTeacherGrade;
import org.mentpeak.test.vo.ReportTeacherGradeVO;

/**
 * 老师年级报告表包装类,返回视图层所需的字段
 *
 * @author lxp
 * @since 2022-07-12
 */
@AllArgsConstructor
public class ReportTeacherGradeWrapper extends BaseEntityWrapper<ReportTeacherGrade, ReportTeacherGradeVO>  {


	@Override
	public ReportTeacherGradeVO entityVO(ReportTeacherGrade reportTeacherGrade) {
		ReportTeacherGradeVO reportTeacherGradeVO = BeanUtil.copy(reportTeacherGrade, ReportTeacherGradeVO.class);


		return reportTeacherGradeVO;
	}

}
