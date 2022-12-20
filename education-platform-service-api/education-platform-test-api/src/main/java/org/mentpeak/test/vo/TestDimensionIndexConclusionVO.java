package org.mentpeak.test.vo;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.mentpeak.test.entity.TestDimensionIndexConclusion;

/**
 * 维度指标结论视图实体类
 *
 * @author lxp
 * @since 2022-07-12
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "TestDimensionIndexConclusionVO对象", description = "维度指标结论")
public class TestDimensionIndexConclusionVO extends TestDimensionIndexConclusion {
	private static final long serialVersionUID = 1L;

}
