package org.mentpeak.test.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.mentpeak.test.entity.TestDimensionIndexQuestion;
import org.mentpeak.test.vo.TestDimensionIndexQuestionVO;

import java.util.List;

/**
 * 维度指标题干关联表 Mapper 接口
 *
 * @author lxp
 * @since 2022-07-12
 */
public interface TestDimensionIndexQuestionMapper extends BaseMapper<TestDimensionIndexQuestion> {

	/**
	 * 自定义分页
	 *
	 * @param page
	 * @param testDimensionIndexQuestion
	 * @return
	 */
	List<TestDimensionIndexQuestionVO> selectTestDimensionIndexQuestionPage(IPage page, TestDimensionIndexQuestionVO testDimensionIndexQuestion);

}
