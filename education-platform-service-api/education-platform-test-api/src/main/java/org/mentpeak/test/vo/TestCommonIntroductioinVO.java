package org.mentpeak.test.vo;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.mentpeak.test.entity.TestCommonIntroductioin;

/**
 * 固定文本视图实体类
 *
 * @author lxp
 * @since 2022-07-12
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "TestCommonIntroductioinVO对象", description = "固定文本")
public class TestCommonIntroductioinVO extends TestCommonIntroductioin {
	private static final long serialVersionUID = 1L;

}
