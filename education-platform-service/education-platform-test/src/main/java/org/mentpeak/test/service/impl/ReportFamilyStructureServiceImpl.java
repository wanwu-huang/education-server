package org.mentpeak.test.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.mentpeak.core.mybatisplus.base.BaseServiceImpl;
import org.mentpeak.test.entity.ReportFamilyStructure;
import org.mentpeak.test.mapper.ReportFamilyStructureMapper;
import org.mentpeak.test.service.IReportFamilyStructureService;
import org.mentpeak.test.vo.ReportFamilyStructureVO;
import org.springframework.stereotype.Service;

/**
 * 家庭结构 服务实现类
 *
 * @author lxp
 * @since 2022-07-12
 */
@Service
public class ReportFamilyStructureServiceImpl extends BaseServiceImpl<ReportFamilyStructureMapper, ReportFamilyStructure> implements IReportFamilyStructureService {

	@Override
	public IPage<ReportFamilyStructureVO> selectReportFamilyStructurePage(IPage<ReportFamilyStructureVO> page, ReportFamilyStructureVO reportFamilyStructure) {
		return page.setRecords(baseMapper.selectReportFamilyStructurePage(page, reportFamilyStructure));
	}

}
