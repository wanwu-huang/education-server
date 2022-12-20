package org.mentpeak.test.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.mentpeak.test.entity.TestTemplate;
import org.mentpeak.test.vo.TestTemplateVO;

import java.util.List;

/**
 * 导入用户模板 Mapper 接口
 *
 * @author lxp
 * @since 2022-08-12
 */
public interface TestTemplateMapper extends BaseMapper<TestTemplate> {

	/**
	 * 自定义分页
	 *
	 * @param page
	 * @param testTemplate
	 * @return
	 */
	List<TestTemplateVO> selectTestTemplatePage(IPage page, TestTemplateVO testTemplate);

}
