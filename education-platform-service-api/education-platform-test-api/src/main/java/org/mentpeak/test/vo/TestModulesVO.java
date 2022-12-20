package org.mentpeak.test.vo;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.mentpeak.test.entity.TestModules;

/**
 * 问卷模块表视图实体类
 *
 * @author lxp
 * @since 2022-07-12
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "TestModulesVO对象", description = "问卷模块表")
public class TestModulesVO extends TestModules {
	private static final long serialVersionUID = 1L;

}
