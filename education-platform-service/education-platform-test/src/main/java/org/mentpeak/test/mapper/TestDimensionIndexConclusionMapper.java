package org.mentpeak.test.mapper;

import com.alicp.jetcache.anno.Cached;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Param;
import org.mentpeak.test.entity.TestDimensionIndexConclusion;
import org.mentpeak.test.vo.TestDimensionIndexConclusionVO;

import java.util.List;

/**
 * 维度指标结论 Mapper 接口
 *
 * @author lxp
 * @since 2022-07-12
 */
public interface TestDimensionIndexConclusionMapper extends BaseMapper<TestDimensionIndexConclusion> {

	/**
	 * 自定义分页
	 *
	 * @param page
	 * @param testDimensionIndexConclusion
	 * @return
	 */
	List<TestDimensionIndexConclusionVO> selectTestDimensionIndexConclusionPage(IPage page, TestDimensionIndexConclusionVO testDimensionIndexConclusion);


	/**
	 * 根据维度ID查询对应结论
	 * @param dimensionId
	 * @return
	 */
	@Cached(name = "education:dimensionIndex:",key = "#dimensionId",expire = 60)
	List<TestDimensionIndexConclusion> dimensionIndexConclusionList(@Param("dimensionId") Long dimensionId);
}
