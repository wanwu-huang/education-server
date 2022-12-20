package org.mentpeak.test.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.mentpeak.core.mybatisplus.base.BaseService;
import org.mentpeak.test.entity.TestDimensionIndexConclusion;
import org.mentpeak.test.vo.TestDimensionIndexConclusionVO;

/**
 * 维度指标结论 服务类
 *
 * @author lxp
 * @since 2022-07-12
 */
public interface ITestDimensionIndexConclusionService extends BaseService<TestDimensionIndexConclusion> {

	/**
	 * 自定义分页
	 *
	 * @param page
	 * @param testDimensionIndexConclusion
	 * @return
	 */
	IPage<TestDimensionIndexConclusionVO> selectTestDimensionIndexConclusionPage(IPage<TestDimensionIndexConclusionVO> page, TestDimensionIndexConclusionVO testDimensionIndexConclusion);

}
