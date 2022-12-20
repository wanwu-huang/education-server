package org.mentpeak.test.vo;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.mentpeak.test.entity.TestOption;

/**
 * 题支信息表视图实体类
 *
 * @author lxp
 * @since 2022-07-12
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "TestOptionVO对象", description = "题支信息表")
public class TestOptionVO extends TestOption {
	private static final long serialVersionUID = 1L;

}
