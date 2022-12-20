package org.mentpeak.test.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.mentpeak.test.entity.TestTeacherQuestion;
import org.mentpeak.test.vo.TestTeacherQuestionVO;

import java.util.List;

/**
 * 教师评定题干信息表 Mapper 接口
 *
 * @author lxp
 * @since 2022-08-15
 */
public interface TestTeacherQuestionMapper extends BaseMapper<TestTeacherQuestion> {

	/**
	 * 自定义分页
	 *
	 * @param page
	 * @param testTeacherQuestion
	 * @return
	 */
	List<TestTeacherQuestionVO> selectTestTeacherQuestionPage(IPage page, TestTeacherQuestionVO testTeacherQuestion);

}
