package org.mentpeak.test.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.mentpeak.test.entity.TestDimensionIndexQuestion;
import org.mentpeak.test.vo.TestDimensionIndexQuestionVO;

/**
 * 维度指标题干关联表 服务类
 *
 * @author lxp
 * @since 2022-07-12
 */
public interface ITestDimensionIndexQuestionService extends IService<TestDimensionIndexQuestion> {

	/**
	 * 自定义分页
	 *
	 * @param page
	 * @param testDimensionIndexQuestion
	 * @return
	 */
	IPage<TestDimensionIndexQuestionVO> selectTestDimensionIndexQuestionPage(IPage<TestDimensionIndexQuestionVO> page, TestDimensionIndexQuestionVO testDimensionIndexQuestion);

}
