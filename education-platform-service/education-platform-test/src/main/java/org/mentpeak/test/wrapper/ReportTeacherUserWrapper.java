package org.mentpeak.test.wrapper;

import lombok.AllArgsConstructor;
import org.mentpeak.core.mybatisplus.support.BaseEntityWrapper;
import org.mentpeak.core.tool.utils.BeanUtil;
import org.mentpeak.test.entity.ReportTeacherUser;
import org.mentpeak.test.vo.ReportTeacherUserVO;

/**
 * 老师用户报告表包装类,返回视图层所需的字段
 *
 * @author lxp
 * @since 2022-07-12
 */
@AllArgsConstructor
public class ReportTeacherUserWrapper extends BaseEntityWrapper<ReportTeacherUser, ReportTeacherUserVO>  {


	@Override
	public ReportTeacherUserVO entityVO(ReportTeacherUser reportTeacherUser) {
		ReportTeacherUserVO reportTeacherUserVO = BeanUtil.copy(reportTeacherUser, ReportTeacherUserVO.class);


		return reportTeacherUserVO;
	}

}
