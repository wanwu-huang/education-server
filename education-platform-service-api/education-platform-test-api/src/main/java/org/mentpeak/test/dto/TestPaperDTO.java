package org.mentpeak.test.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.mentpeak.test.entity.TestPaper;

/**
 * 用户试卷信息表数据传输对象实体类
 *
 * @author lxp
 * @since 2022-07-12
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class TestPaperDTO extends TestPaper {
	private static final long serialVersionUID = 1L;

}
