package org.mentpeak.test.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.mentpeak.test.entity.TestCommonIntroductioin;

/**
 * 固定文本数据传输对象实体类
 *
 * @author lxp
 * @since 2022-07-12
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class TestCommonIntroductioinDTO extends TestCommonIntroductioin {
	private static final long serialVersionUID = 1L;

}
