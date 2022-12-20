package org.mentpeak.test.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.mentpeak.test.entity.TestTemplate;
import org.mentpeak.test.mapper.TestTemplateMapper;
import org.mentpeak.test.service.ITestTemplateService;
import org.mentpeak.test.vo.TestTemplateVO;
import org.springframework.stereotype.Service;

/**
 * 导入用户模板 服务实现类
 *
 * @author lxp
 * @since 2022-08-12
 */
@Service
public class TestTemplateServiceImpl extends ServiceImpl<TestTemplateMapper, TestTemplate> implements ITestTemplateService {

	@Override
	public IPage<TestTemplateVO> selectTestTemplatePage(IPage<TestTemplateVO> page, TestTemplateVO testTemplate) {
		return page.setRecords(baseMapper.selectTestTemplatePage(page, testTemplate));
	}


	@Override
	public TestTemplate getImportUserInfoTemplate(Integer type) {
		TestTemplate testTemplate = baseMapper.selectOne(Wrappers.<TestTemplate>lambdaQuery().eq(TestTemplate::getType, type));
		return testTemplate;
	}
}
