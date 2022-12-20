package org.mentpeak.test.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.mentpeak.core.mybatisplus.base.BaseService;
import org.mentpeak.test.entity.TestQuestion;
import org.mentpeak.test.vo.TestQuestionVO;

/**
 * 题干信息表 服务类
 *
 * @author lxp
 * @since 2022-07-12
 */
public interface ITestQuestionService extends BaseService<TestQuestion> {

	/**
	 * 自定义分页
	 *
	 * @param page
	 * @param testQuestion
	 * @return
	 */
	IPage<TestQuestionVO> selectTestQuestionPage(IPage<TestQuestionVO> page, TestQuestionVO testQuestion);

}
