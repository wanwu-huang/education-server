package org.mentpeak.test.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.mentpeak.core.mybatisplus.base.BaseService;
import org.mentpeak.test.entity.TestTeacherQuestion;
import org.mentpeak.test.vo.TestTeacherQuestionVO;

/**
 * 教师评定题干信息表 服务类
 *
 * @author lxp
 * @since 2022-08-15
 */
public interface ITestTeacherQuestionService extends BaseService<TestTeacherQuestion> {

	/**
	 * 自定义分页
	 *
	 * @param page
	 * @param testTeacherQuestion
	 * @return
	 */
	IPage<TestTeacherQuestionVO> selectTestTeacherQuestionPage(IPage<TestTeacherQuestionVO> page, TestTeacherQuestionVO testTeacherQuestion);

}
