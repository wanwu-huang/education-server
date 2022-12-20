package org.mentpeak.test.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.mentpeak.core.mybatisplus.base.BaseServiceImpl;
import org.mentpeak.test.entity.TestTeacherQuestion;
import org.mentpeak.test.mapper.TestTeacherQuestionMapper;
import org.mentpeak.test.service.ITestTeacherQuestionService;
import org.mentpeak.test.vo.TestTeacherQuestionVO;
import org.springframework.stereotype.Service;

/**
 * 教师评定题干信息表 服务实现类
 *
 * @author lxp
 * @since 2022-08-15
 */
@Service
public class TestTeacherQuestionServiceImpl extends BaseServiceImpl<TestTeacherQuestionMapper, TestTeacherQuestion> implements ITestTeacherQuestionService {

	@Override
	public IPage<TestTeacherQuestionVO> selectTestTeacherQuestionPage(IPage<TestTeacherQuestionVO> page, TestTeacherQuestionVO testTeacherQuestion) {
		return page.setRecords(baseMapper.selectTestTeacherQuestionPage(page, testTeacherQuestion));
	}

}
