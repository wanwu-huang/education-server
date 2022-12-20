package org.mentpeak.test.mapper;

import com.alicp.jetcache.anno.Cached;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.mentpeak.test.entity.TestQuestion;
import org.mentpeak.test.vo.TestQuestionVO;

import java.util.List;

/**
 * 题干信息表 Mapper 接口
 *
 * @author lxp
 * @since 2022-07-12
 */
public interface TestQuestionMapper extends BaseMapper<TestQuestion> {

	/**
	 * 自定义分页
	 *
	 * @param page
	 * @param testQuestion
	 * @return
	 */
	List<TestQuestionVO> selectTestQuestionPage(IPage page, TestQuestionVO testQuestion);

	/**
	 * 根据指标ID获取题目信息
	 * @param indexId
	 * @return
	 */
	@Cached(name = "education:questionInfo:",key = "#indexId",expire = 60)
	List<TestQuestion> questionList(Long indexId);
}
