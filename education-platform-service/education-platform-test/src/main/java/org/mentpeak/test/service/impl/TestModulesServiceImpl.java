package org.mentpeak.test.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.mentpeak.core.mybatisplus.base.BaseServiceImpl;
import org.mentpeak.test.entity.TestModules;
import org.mentpeak.test.mapper.TestModulesMapper;
import org.mentpeak.test.service.ITestModulesService;
import org.mentpeak.test.vo.TestModulesVO;
import org.springframework.stereotype.Service;

/**
 * 问卷模块表 服务实现类
 *
 * @author lxp
 * @since 2022-07-12
 */
@Service
public class TestModulesServiceImpl extends BaseServiceImpl<TestModulesMapper, TestModules> implements ITestModulesService {

	@Override
	public IPage<TestModulesVO> selectTestModulesPage(IPage<TestModulesVO> page, TestModulesVO testModules) {
		return page.setRecords(baseMapper.selectTestModulesPage(page, testModules));
	}

}
