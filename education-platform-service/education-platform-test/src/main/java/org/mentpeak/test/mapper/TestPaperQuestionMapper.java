package org.mentpeak.test.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Param;
import org.mentpeak.parent.vo.ParentOptionVO;
import org.mentpeak.test.entity.TestPaperQuestion;
import org.mentpeak.test.vo.ParentPaperQuestionVO;
import org.mentpeak.test.vo.TestPaperQuestionVO;

import java.util.List;

/**
 * 试卷和题干及选中题支关系表 Mapper 接口
 *
 * @author lxp
 * @since 2022-07-15
 */
public interface TestPaperQuestionMapper extends BaseMapper<TestPaperQuestion> {

	/**
	 * 自定义分页
	 *
	 * @param page
	 * @param testPaperQuestion
	 * @return
	 */
	List<TestPaperQuestionVO> selectTestPaperQuestionPage(IPage page, TestPaperQuestionVO testPaperQuestion);


	/**
	 * 指标总分数
	 *
	 * @param indexId 指标ID
	 * @param paperId 试卷ID
	 * @return 指标分数
	 */
	Integer indexTotalScore(@Param("indexId") Long indexId, @Param("paperId") Long paperId);

	/**
	 * 维度总分数
	 *
	 * @param dimensionId 维度ID
	 * @param paperId     试卷ID
	 * @return 维度分数
	 */
	Integer dimensionTotalScore(@Param("dimensionId") Long dimensionId, @Param("paperId") Long paperId);


	/**
	 * 数据有效性总分数
	 *
	 * @param paperId 试卷ID
	 * @return 数据有效性分数
	 */
	Integer validityAnswersScore( @Param("paperId") Long paperId);


	/**
	 * 根据按用户id查询最新试卷ID
	 *
	 * @param userId 用户id
	 * @return 试卷ID
	 */
	Long getPaperIdByUserId( @Param("userId") Long userId);

	/**
	 * 根据试卷ID获取做题选项
	 * @param paperId
	 * @return
	 */
	List<ParentPaperQuestionVO> getParentPaperQuestionByPaperId(@Param("paperId") Long paperId);

	/**
	 * 获取选项分
	 * @param ids
	 * @return
	 */
	Integer getOptionScore(List<String> ids);

	/**
	 * 根据试卷ID获取家庭题做题选项
	 * @param paperId
	 * @param questionId
	 * @return
	 */
	ParentPaperQuestionVO getParentPaperQuestionByPaperIdQuestionId(@Param("paperId") Long paperId,@Param("questionId") Long questionId);

	/**
	 * 获取选项信息
	 * @param id
	 * @return
	 */
	ParentOptionVO getOptionInfoById(@Param("id") Long id);
}
