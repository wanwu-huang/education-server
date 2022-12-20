package org.mentpeak.test.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.mentpeak.core.mybatisplus.base.BaseServiceImpl;
import org.mentpeak.test.entity.TestQuestion;
import org.mentpeak.test.mapper.TestQuestionMapper;
import org.mentpeak.test.service.ITestQuestionService;
import org.mentpeak.test.vo.TestQuestionVO;
import org.springframework.stereotype.Service;

/**
 * 题干信息表 服务实现类
 *
 * @author lxp
 * @since 2022-07-12
 */
@Service
public class TestQuestionServiceImpl extends BaseServiceImpl<TestQuestionMapper, TestQuestion> implements ITestQuestionService {

	@Override
	public IPage<TestQuestionVO> selectTestQuestionPage(IPage<TestQuestionVO> page, TestQuestionVO testQuestion) {
		return page.setRecords(baseMapper.selectTestQuestionPage(page, testQuestion));
	}

}
