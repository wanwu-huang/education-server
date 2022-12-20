package org.mentpeak.test.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.mentpeak.test.entity.TestTemplate;
import org.mentpeak.test.vo.TestTemplateVO;

/**
 * 导入用户模板 服务类
 *
 * @author lxp
 * @since 2022-08-12
 */
public interface ITestTemplateService extends IService<TestTemplate> {

	/**
	 * 自定义分页
	 *
	 * @param page
	 * @param testTemplate
	 * @return
	 */
	IPage<TestTemplateVO> selectTestTemplatePage(IPage<TestTemplateVO> page, TestTemplateVO testTemplate);

	/**
	 * 导入用户模板
	 * @param type  组织类别
	 * @return
	 */
	TestTemplate getImportUserInfoTemplate(Integer type);
}
