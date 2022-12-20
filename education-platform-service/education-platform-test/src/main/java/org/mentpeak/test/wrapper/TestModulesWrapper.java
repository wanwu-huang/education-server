package org.mentpeak.test.wrapper;

import lombok.AllArgsConstructor;
import org.mentpeak.core.mybatisplus.support.BaseEntityWrapper;
import org.mentpeak.core.tool.utils.BeanUtil;
import org.mentpeak.test.entity.TestModules;
import org.mentpeak.test.vo.TestModulesVO;

/**
 * 问卷模块表包装类,返回视图层所需的字段
 *
 * @author lxp
 * @since 2022-07-12
 */
@AllArgsConstructor
public class TestModulesWrapper extends BaseEntityWrapper<TestModules, TestModulesVO>  {


	@Override
	public TestModulesVO entityVO(TestModules testModules) {
		TestModulesVO testModulesVO = BeanUtil.copy(testModules, TestModulesVO.class);


		return testModulesVO;
	}

}
