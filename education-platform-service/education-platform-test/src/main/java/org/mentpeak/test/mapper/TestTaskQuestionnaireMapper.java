package org.mentpeak.test.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Param;
import org.mentpeak.test.entity.TestTaskQuestionnaire;
import org.mentpeak.test.vo.TaskQuestionnaireVO;
import org.mentpeak.test.vo.TestTaskQuestionnaireVO;

import java.util.List;

/**
 * 测评任务问卷关联表 Mapper 接口
 *
 * @author lxp
 * @since 2022-07-12
 */
public interface TestTaskQuestionnaireMapper extends BaseMapper<TestTaskQuestionnaire> {

	/**
	 * 自定义分页
	 *
	 * @param page
	 * @param testTaskQuestionnaire
	 * @return
	 */
	List<TestTaskQuestionnaireVO> selectTestTaskQuestionnairePage(IPage page, TestTaskQuestionnaireVO testTaskQuestionnaire);

	List<TaskQuestionnaireVO> getListById(@Param("taskId") Long taskId);

}
