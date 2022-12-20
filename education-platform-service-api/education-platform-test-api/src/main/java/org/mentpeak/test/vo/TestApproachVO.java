package org.mentpeak.test.vo;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.mentpeak.test.entity.TestApproach;

/**
 * 测评途径表视图实体类
 *
 * @author lxp
 * @since 2022-07-12
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "TestApproachVO对象", description = "测评途径表")
public class TestApproachVO extends TestApproach {
	private static final long serialVersionUID = 1L;

}
