package org.mentpeak.test.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.mentpeak.core.mybatisplus.base.BaseServiceImpl;
import org.mentpeak.test.entity.ReportTeacherUser;
import org.mentpeak.test.mapper.ReportTeacherUserMapper;
import org.mentpeak.test.service.IReportTeacherUserService;
import org.mentpeak.test.vo.ReportTeacherUserVO;
import org.springframework.stereotype.Service;

/**
 * 老师用户报告表 服务实现类
 *
 * @author lxp
 * @since 2022-07-12
 */
@Service
public class ReportTeacherUserServiceImpl extends BaseServiceImpl<ReportTeacherUserMapper, ReportTeacherUser> implements IReportTeacherUserService {

	@Override
	public IPage<ReportTeacherUserVO> selectReportTeacherUserPage(IPage<ReportTeacherUserVO> page, ReportTeacherUserVO reportTeacherUser) {
		return page.setRecords(baseMapper.selectReportTeacherUserPage(page, reportTeacherUser));
	}

}
