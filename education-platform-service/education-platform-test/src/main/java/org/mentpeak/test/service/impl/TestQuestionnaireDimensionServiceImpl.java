package org.mentpeak.test.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.mentpeak.core.mybatisplus.base.BaseServiceImpl;
import org.mentpeak.test.entity.TestQuestionnaireDimension;
import org.mentpeak.test.mapper.TestQuestionnaireDimensionMapper;
import org.mentpeak.test.service.ITestQuestionnaireDimensionService;
import org.mentpeak.test.vo.TestQuestionnaireDimensionVO;
import org.springframework.stereotype.Service;

/**
 * 问卷维度关联表 服务实现类
 *
 * @author lxp
 * @since 2022-07-12
 */
@Service
public class TestQuestionnaireDimensionServiceImpl extends BaseServiceImpl<TestQuestionnaireDimensionMapper, TestQuestionnaireDimension> implements ITestQuestionnaireDimensionService {

	@Override
	public IPage<TestQuestionnaireDimensionVO> selectTestQuestionnaireDimensionPage(IPage<TestQuestionnaireDimensionVO> page, TestQuestionnaireDimensionVO testQuestionnaireDimension) {
		return page.setRecords(baseMapper.selectTestQuestionnaireDimensionPage(page, testQuestionnaireDimension));
	}

}
