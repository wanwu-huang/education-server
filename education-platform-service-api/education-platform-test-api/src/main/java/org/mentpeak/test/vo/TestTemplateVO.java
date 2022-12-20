package org.mentpeak.test.vo;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.mentpeak.test.entity.TestTemplate;

/**
 * 导入用户模板视图实体类
 *
 * @author lxp
 * @since 2022-08-12
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "TestTemplateVO对象", description = "导入用户模板")
public class TestTemplateVO extends TestTemplate {
	private static final long serialVersionUID = 1L;

}
