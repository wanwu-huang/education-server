package org.mentpeak.test.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.mentpeak.core.mybatisplus.base.BaseServiceImpl;
import org.mentpeak.test.entity.TestDimension;
import org.mentpeak.test.mapper.TestDimensionMapper;
import org.mentpeak.test.service.ITestDimensionService;
import org.mentpeak.test.vo.TestDimensionVO;
import org.springframework.stereotype.Service;

/**
 * 维度表 服务实现类
 *
 * @author lxp
 * @since 2022-07-12
 */
@Service
public class TestDimensionServiceImpl extends BaseServiceImpl<TestDimensionMapper, TestDimension> implements ITestDimensionService {

	@Override
	public IPage<TestDimensionVO> selectTestDimensionPage(IPage<TestDimensionVO> page, TestDimensionVO testDimension) {
		return page.setRecords(baseMapper.selectTestDimensionPage(page, testDimension));
	}

}
