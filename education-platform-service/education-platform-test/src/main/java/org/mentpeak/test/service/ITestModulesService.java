package org.mentpeak.test.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.mentpeak.core.mybatisplus.base.BaseService;
import org.mentpeak.test.entity.TestModules;
import org.mentpeak.test.vo.TestModulesVO;

/**
 * 问卷模块表 服务类
 *
 * @author lxp
 * @since 2022-07-12
 */
public interface ITestModulesService extends BaseService<TestModules> {

	/**
	 * 自定义分页
	 *
	 * @param page
	 * @param testModules
	 * @return
	 */
	IPage<TestModulesVO> selectTestModulesPage(IPage<TestModulesVO> page, TestModulesVO testModules);

}
