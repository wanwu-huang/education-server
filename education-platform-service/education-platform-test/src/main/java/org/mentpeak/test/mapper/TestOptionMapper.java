package org.mentpeak.test.mapper;

import com.alicp.jetcache.anno.Cached;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Param;
import org.mentpeak.test.entity.TestOption;
import org.mentpeak.test.vo.TestOptionVO;

import java.util.List;

/**
 * 题支信息表 Mapper 接口
 *
 * @author lxp
 * @since 2022-07-12
 */
public interface TestOptionMapper extends BaseMapper<TestOption> {

	/**
	 * 自定义分页
	 *
	 * @param page
	 * @param testOption
	 * @return
	 */
	List<TestOptionVO> selectTestOptionPage(IPage page, TestOptionVO testOption);

	/**
	 * 选项分数列表
	 * @param paperId
	 * @return
	 */
	List<String> optionScoreList(@Param("paperId") Long paperId);


	/**
	 * 根据指标ID 分数 查询选项ID
	 * @param indexId 指标ID
	 * @param score 分数
	 * @return
	 */
	List<String> optionIdList(@Param("indexId") Long indexId,@Param("score") Integer score);

	/**
	 * 根据题干ID 查询选项ID
	 * @param questionId 题干ID
	 * @return
	 */
	@Cached(name = "education:optionIdInfo:",key = "#questionId",expire = 60)
	List<String> optionIdListByQuestionId(@Param("questionId") Long questionId);
}
