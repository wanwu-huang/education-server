package org.mentpeak.test.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.mentpeak.core.mybatisplus.base.BaseService;
import org.mentpeak.test.entity.TestDimension;
import org.mentpeak.test.vo.TestDimensionVO;

/**
 * 维度表 服务类
 *
 * @author lxp
 * @since 2022-07-12
 */
public interface ITestDimensionService extends BaseService<TestDimension> {

	/**
	 * 自定义分页
	 *
	 * @param page
	 * @param testDimension
	 * @return
	 */
	IPage<TestDimensionVO> selectTestDimensionPage(IPage<TestDimensionVO> page, TestDimensionVO testDimension);

}
