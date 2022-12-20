package org.mentpeak.test.vo;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.mentpeak.test.entity.TestDimensionIndexQuestion;

/**
 * 维度指标题干关联表视图实体类
 *
 * @author lxp
 * @since 2022-07-12
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "TestDimensionIndexQuestionVO对象", description = "维度指标题干关联表")
public class TestDimensionIndexQuestionVO extends TestDimensionIndexQuestion {
	private static final long serialVersionUID = 1L;

}
