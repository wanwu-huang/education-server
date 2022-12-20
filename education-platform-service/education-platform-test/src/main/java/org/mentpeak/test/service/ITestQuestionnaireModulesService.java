package org.mentpeak.test.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.mentpeak.core.mybatisplus.base.BaseService;
import org.mentpeak.test.entity.TestQuestionnaireModules;
import org.mentpeak.test.vo.TestQuestionnaireModulesVO;

/**
 * 问卷模块关联表 服务类
 *
 * @author lxp
 * @since 2022-07-12
 */
public interface ITestQuestionnaireModulesService extends BaseService<TestQuestionnaireModules> {

	/**
	 * 自定义分页
	 *
	 * @param page
	 * @param testQuestionnaireModules
	 * @return
	 */
	IPage<TestQuestionnaireModulesVO> selectTestQuestionnaireModulesPage(IPage<TestQuestionnaireModulesVO> page, TestQuestionnaireModulesVO testQuestionnaireModules);

}
