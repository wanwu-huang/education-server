package org.mentpeak.test.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.mentpeak.core.mybatisplus.base.BaseService;
import org.mentpeak.test.entity.ReportTeacherUser;
import org.mentpeak.test.vo.ReportTeacherUserVO;

/**
 * 老师用户报告表 服务类
 *
 * @author lxp
 * @since 2022-07-12
 */
public interface IReportTeacherUserService extends BaseService<ReportTeacherUser> {

	/**
	 * 自定义分页
	 *
	 * @param page
	 * @param reportTeacherUser
	 * @return
	 */
	IPage<ReportTeacherUserVO> selectReportTeacherUserPage(IPage<ReportTeacherUserVO> page, ReportTeacherUserVO reportTeacherUser);

}
