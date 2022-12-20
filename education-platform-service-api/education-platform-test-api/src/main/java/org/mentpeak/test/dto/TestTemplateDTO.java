package org.mentpeak.test.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.mentpeak.test.entity.TestTemplate;

/**
 * 导入用户模板数据传输对象实体类
 *
 * @author lxp
 * @since 2022-08-12
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class TestTemplateDTO extends TestTemplate {
	private static final long serialVersionUID = 1L;

}
