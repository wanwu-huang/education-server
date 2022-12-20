package org.mentpeak.test.wrapper;

import lombok.AllArgsConstructor;
import org.mentpeak.core.mybatisplus.support.BaseEntityWrapper;
import org.mentpeak.core.tool.utils.BeanUtil;
import org.mentpeak.test.entity.TestTemplate;
import org.mentpeak.test.vo.TestTemplateVO;

/**
 * 导入用户模板包装类,返回视图层所需的字段
 *
 * @author lxp
 * @since 2022-08-12
 */
@AllArgsConstructor
public class TestTemplateWrapper extends BaseEntityWrapper<TestTemplate, TestTemplateVO>  {


	@Override
	public TestTemplateVO entityVO(TestTemplate testTemplate) {
		TestTemplateVO testTemplateVO = BeanUtil.copy(testTemplate, TestTemplateVO.class);


		return testTemplateVO;
	}

}
