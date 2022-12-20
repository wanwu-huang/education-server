package org.mentpeak.test.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.mentpeak.test.entity.TestDimensionIndexQuestion;
import org.mentpeak.test.mapper.TestDimensionIndexQuestionMapper;
import org.mentpeak.test.service.ITestDimensionIndexQuestionService;
import org.mentpeak.test.vo.TestDimensionIndexQuestionVO;
import org.springframework.stereotype.Service;

/**
 * 维度指标题干关联表 服务实现类
 *
 * @author lxp
 * @since 2022-07-12
 */
@Service
public class TestDimensionIndexQuestionServiceImpl extends ServiceImpl<TestDimensionIndexQuestionMapper, TestDimensionIndexQuestion> implements ITestDimensionIndexQuestionService {

	@Override
	public IPage<TestDimensionIndexQuestionVO> selectTestDimensionIndexQuestionPage(IPage<TestDimensionIndexQuestionVO> page, TestDimensionIndexQuestionVO testDimensionIndexQuestion) {
		return page.setRecords(baseMapper.selectTestDimensionIndexQuestionPage(page, testDimensionIndexQuestion));
	}

}
