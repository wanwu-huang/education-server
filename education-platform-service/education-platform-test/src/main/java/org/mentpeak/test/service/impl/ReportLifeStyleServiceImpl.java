package org.mentpeak.test.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.mentpeak.core.mybatisplus.base.BaseServiceImpl;
import org.mentpeak.test.entity.ReportLifeStyle;
import org.mentpeak.test.mapper.ReportLifeStyleMapper;
import org.mentpeak.test.service.IReportLifeStyleService;
import org.mentpeak.test.vo.ReportLifeStyleVO;
import org.springframework.stereotype.Service;

/**
 * 生活方式（是否与父母生活） 服务实现类
 *
 * @author lxp
 * @since 2022-07-12
 */
@Service
public class ReportLifeStyleServiceImpl extends BaseServiceImpl<ReportLifeStyleMapper, ReportLifeStyle> implements IReportLifeStyleService {

	@Override
	public IPage<ReportLifeStyleVO> selectReportLifeStylePage(IPage<ReportLifeStyleVO> page, ReportLifeStyleVO reportLifeStyle) {
		return page.setRecords(baseMapper.selectReportLifeStylePage(page, reportLifeStyle));
	}

}
