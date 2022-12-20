package org.mentpeak.test.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.mentpeak.core.mybatisplus.base.BaseServiceImpl;
import org.mentpeak.test.entity.ReportLifeStyleTaskUser;
import org.mentpeak.test.mapper.ReportLifeStyleTaskUserMapper;
import org.mentpeak.test.service.IReportLifeStyleTaskUserService;
import org.mentpeak.test.vo.ReportLifeStyleTaskUserVO;
import org.springframework.stereotype.Service;

/**
 * 生活方式任务用户关联表 服务实现类
 *
 * @author lxp
 * @since 2022-07-12
 */
@Service
public class ReportLifeStyleTaskUserServiceImpl extends BaseServiceImpl<ReportLifeStyleTaskUserMapper, ReportLifeStyleTaskUser> implements IReportLifeStyleTaskUserService {

	@Override
	public IPage<ReportLifeStyleTaskUserVO> selectReportLifeStyleTaskUserPage(IPage<ReportLifeStyleTaskUserVO> page, ReportLifeStyleTaskUserVO reportLifeStyleTaskUser) {
		return page.setRecords(baseMapper.selectReportLifeStyleTaskUserPage(page, reportLifeStyleTaskUser));
	}

}
