package org.mentpeak.test.vo;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.mentpeak.test.entity.TestIndex;

/**
 * 指标表视图实体类
 *
 * @author lxp
 * @since 2022-07-12
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "TestIndexVO对象", description = "指标表")
public class TestIndexVO extends TestIndex {
	private static final long serialVersionUID = 1L;

}
