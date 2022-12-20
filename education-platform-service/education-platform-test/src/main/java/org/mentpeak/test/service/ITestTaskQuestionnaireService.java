package org.mentpeak.test.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.mentpeak.test.entity.TestTaskQuestionnaire;
import org.mentpeak.test.vo.TaskQuestionnaireVO;
import org.mentpeak.test.vo.TestTaskQuestionnaireVO;

import java.util.List;

/**
 * 测评任务问卷关联表 服务类
 *
 * @author lxp
 * @since 2022-07-12
 */
public interface ITestTaskQuestionnaireService extends IService<TestTaskQuestionnaire> {

	/**
	 * 自定义分页
	 *
	 * @param page
	 * @param testTaskQuestionnaire
	 * @return
	 */
	IPage<TestTaskQuestionnaireVO> selectTestTaskQuestionnairePage(IPage<TestTaskQuestionnaireVO> page, TestTaskQuestionnaireVO testTaskQuestionnaire);

	List<TaskQuestionnaireVO> getListById(Long id);

}
