package org.mentpeak.test.vo;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.mentpeak.test.entity.TestDimension;

/**
 * 维度表视图实体类
 *
 * @author lxp
 * @since 2022-07-12
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "TestDimensionVO对象", description = "维度表")
public class TestDimensionVO extends TestDimension {
	private static final long serialVersionUID = 1L;

}
