package org.mentpeak.test.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.mentpeak.core.mybatisplus.base.BaseServiceImpl;
import org.mentpeak.test.entity.TestQuestionnaireModules;
import org.mentpeak.test.mapper.TestQuestionnaireModulesMapper;
import org.mentpeak.test.service.ITestQuestionnaireModulesService;
import org.mentpeak.test.vo.TestQuestionnaireModulesVO;
import org.springframework.stereotype.Service;

/**
 * 问卷模块关联表 服务实现类
 *
 * @author lxp
 * @since 2022-07-12
 */
@Service
public class TestQuestionnaireModulesServiceImpl extends BaseServiceImpl<TestQuestionnaireModulesMapper, TestQuestionnaireModules> implements ITestQuestionnaireModulesService {

	@Override
	public IPage<TestQuestionnaireModulesVO> selectTestQuestionnaireModulesPage(IPage<TestQuestionnaireModulesVO> page, TestQuestionnaireModulesVO testQuestionnaireModules) {
		return page.setRecords(baseMapper.selectTestQuestionnaireModulesPage(page, testQuestionnaireModules));
	}

}
