package org.mentpeak.test.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.mentpeak.core.mybatisplus.base.BaseServiceImpl;
import org.mentpeak.test.entity.TestParentConclusion;
import org.mentpeak.test.mapper.TestParentConclusionMapper;
import org.mentpeak.test.service.ITestParentConclusionService;
import org.mentpeak.test.vo.TestParentConclusionVO;
import org.springframework.stereotype.Service;

/**
 * 家长他评结论 服务实现类
 *
 * @author lxp
 * @since 2022-07-12
 */
@Service
public class TestParentConclusionServiceImpl extends BaseServiceImpl<TestParentConclusionMapper, TestParentConclusion> implements ITestParentConclusionService {

	@Override
	public IPage<TestParentConclusionVO> selectTestParentConclusionPage(IPage<TestParentConclusionVO> page, TestParentConclusionVO testParentConclusion) {
		return page.setRecords(baseMapper.selectTestParentConclusionPage(page, testParentConclusion));
	}

}
