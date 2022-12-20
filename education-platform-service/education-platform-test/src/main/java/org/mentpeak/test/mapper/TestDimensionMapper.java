package org.mentpeak.test.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.mentpeak.test.entity.TestDimension;
import org.mentpeak.test.vo.TestDimensionVO;

import java.util.List;

/**
 * 维度表 Mapper 接口
 *
 * @author lxp
 * @since 2022-07-12
 */
public interface TestDimensionMapper extends BaseMapper<TestDimension> {

	/**
	 * 自定义分页
	 *
	 * @param page
	 * @param testDimension
	 * @return
	 */
	List<TestDimensionVO> selectTestDimensionPage(IPage page, TestDimensionVO testDimension);

	/**
	 * 获取没有指标的维度信息
	 * @return
	 */
	List<TestDimension> dimensionList();

}
