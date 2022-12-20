package org.mentpeak.test.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.mentpeak.test.entity.TestQuestionnaireModules;
import org.mentpeak.test.vo.TestQuestionnaireModulesVO;

import java.util.List;

/**
 * 问卷模块关联表 Mapper 接口
 *
 * @author lxp
 * @since 2022-07-12
 */
public interface TestQuestionnaireModulesMapper extends BaseMapper<TestQuestionnaireModules> {

	/**
	 * 自定义分页
	 *
	 * @param page
	 * @param testQuestionnaireModules
	 * @return
	 */
	List<TestQuestionnaireModulesVO> selectTestQuestionnaireModulesPage(IPage page, TestQuestionnaireModulesVO testQuestionnaireModules);

}
