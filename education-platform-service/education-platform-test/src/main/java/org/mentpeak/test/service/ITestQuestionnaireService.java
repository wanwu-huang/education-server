package org.mentpeak.test.service;

import com.alicp.jetcache.anno.Cached;
import org.mentpeak.test.entity.TestQuestionnaire;
import org.mentpeak.test.vo.TestQuestionnaireVO;
import org.mentpeak.core.mybatisplus.base.BaseService;
import com.baomidou.mybatisplus.core.metadata.IPage;

import java.util.List;

/**
 * 问卷表 服务类
 *
 * @author lxp
 * @since 2022-07-13
 */
public interface ITestQuestionnaireService extends BaseService<TestQuestionnaire> {

	/**
	 * 自定义分页
	 *
	 * @param page
	 * @param testQuestionnaire
	 * @return
	 */
	IPage<TestQuestionnaireVO> selectTestQuestionnairePage(IPage<TestQuestionnaireVO> page, TestQuestionnaireVO testQuestionnaire);


	/**
	 * 测评问卷列表
	 * @return list
	 */
	List<TestQuestionnaire> testQuestionnaireList();
}
