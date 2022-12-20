package org.mentpeak.test.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.mentpeak.test.entity.TestQuestionnaire;
import org.mentpeak.test.vo.TestQuestionnaireVO;
import org.mentpeak.test.mapper.TestQuestionnaireMapper;
import org.mentpeak.test.service.ITestQuestionnaireService;
import org.mentpeak.core.mybatisplus.base.BaseServiceImpl;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.metadata.IPage;

import java.util.List;

/**
 * 问卷表 服务实现类
 *
 * @author lxp
 * @since 2022-07-13
 */
@Service
public class TestQuestionnaireServiceImpl extends BaseServiceImpl<TestQuestionnaireMapper, TestQuestionnaire> implements ITestQuestionnaireService {

	@Override
	public IPage<TestQuestionnaireVO> selectTestQuestionnairePage(IPage<TestQuestionnaireVO> page, TestQuestionnaireVO testQuestionnaire) {
		return page.setRecords(baseMapper.selectTestQuestionnairePage(page, testQuestionnaire));
	}

	@Override
	public List<TestQuestionnaire> testQuestionnaireList() {
		return baseMapper.selectList(Wrappers.<TestQuestionnaire>lambdaQuery()
				.eq(TestQuestionnaire::getType,3).eq(TestQuestionnaire::getStatus,1).orderByAsc(TestQuestionnaire::getSort));
	}

}
