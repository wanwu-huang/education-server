package org.mentpeak.test.wrapper;

import lombok.AllArgsConstructor;
import org.mentpeak.core.mybatisplus.support.BaseEntityWrapper;
import org.mentpeak.core.tool.utils.BeanUtil;
import org.mentpeak.test.entity.ReportTeacherClass;
import org.mentpeak.test.vo.ReportTeacherClassVO;

/**
 * 老师班级报告表包装类,返回视图层所需的字段
 *
 * @author lxp
 * @since 2022-07-12
 */
@AllArgsConstructor
public class ReportTeacherClassWrapper extends BaseEntityWrapper<ReportTeacherClass, ReportTeacherClassVO>  {


	@Override
	public ReportTeacherClassVO entityVO(ReportTeacherClass reportTeacherClass) {
		ReportTeacherClassVO reportTeacherClassVO = BeanUtil.copy(reportTeacherClass, ReportTeacherClassVO.class);


		return reportTeacherClassVO;
	}

}
