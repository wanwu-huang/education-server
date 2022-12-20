package org.mentpeak.test.mapper;

import org.mentpeak.test.entity.TestQuestionnaire;
import org.mentpeak.test.vo.TestQuestionnaireVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import java.util.List;

/**
 * 问卷表 Mapper 接口
 *
 * @author lxp
 * @since 2022-07-13
 */
public interface TestQuestionnaireMapper extends BaseMapper<TestQuestionnaire> {

	/**
	 * 自定义分页
	 *
	 * @param page
	 * @param testQuestionnaire
	 * @return
	 */
	List<TestQuestionnaireVO> selectTestQuestionnairePage(IPage page, TestQuestionnaireVO testQuestionnaire);

}
