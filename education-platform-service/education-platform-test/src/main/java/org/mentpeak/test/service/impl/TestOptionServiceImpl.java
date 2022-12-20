package org.mentpeak.test.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.mentpeak.core.mybatisplus.base.BaseServiceImpl;
import org.mentpeak.test.entity.TestOption;
import org.mentpeak.test.mapper.TestOptionMapper;
import org.mentpeak.test.service.ITestOptionService;
import org.mentpeak.test.vo.TestOptionVO;
import org.springframework.stereotype.Service;

/**
 * 题支信息表 服务实现类
 *
 * @author lxp
 * @since 2022-07-12
 */
@Service
public class TestOptionServiceImpl extends BaseServiceImpl<TestOptionMapper, TestOption> implements ITestOptionService {

	@Override
	public IPage<TestOptionVO> selectTestOptionPage(IPage<TestOptionVO> page, TestOptionVO testOption) {
		return page.setRecords(baseMapper.selectTestOptionPage(page, testOption));
	}

}
