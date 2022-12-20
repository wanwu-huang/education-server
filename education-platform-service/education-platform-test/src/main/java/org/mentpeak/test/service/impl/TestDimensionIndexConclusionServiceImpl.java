package org.mentpeak.test.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.mentpeak.core.mybatisplus.base.BaseServiceImpl;
import org.mentpeak.test.entity.TestDimensionIndexConclusion;
import org.mentpeak.test.mapper.TestDimensionIndexConclusionMapper;
import org.mentpeak.test.service.ITestDimensionIndexConclusionService;
import org.mentpeak.test.vo.TestDimensionIndexConclusionVO;
import org.springframework.stereotype.Service;

/**
 * 维度指标结论 服务实现类
 *
 * @author lxp
 * @since 2022-07-12
 */
@Service
public class TestDimensionIndexConclusionServiceImpl extends BaseServiceImpl<TestDimensionIndexConclusionMapper, TestDimensionIndexConclusion> implements ITestDimensionIndexConclusionService {

	@Override
	public IPage<TestDimensionIndexConclusionVO> selectTestDimensionIndexConclusionPage(IPage<TestDimensionIndexConclusionVO> page, TestDimensionIndexConclusionVO testDimensionIndexConclusion) {
		return page.setRecords(baseMapper.selectTestDimensionIndexConclusionPage(page, testDimensionIndexConclusion));
	}

}
