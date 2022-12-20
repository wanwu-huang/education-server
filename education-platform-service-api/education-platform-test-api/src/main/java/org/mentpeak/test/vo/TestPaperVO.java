package org.mentpeak.test.vo;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.mentpeak.test.entity.TestPaper;

/**
 * 用户试卷信息表视图实体类
 *
 * @author lxp
 * @since 2022-07-12
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "TestPaperVO对象", description = "用户试卷信息表")
public class TestPaperVO extends TestPaper {
	private static final long serialVersionUID = 1L;

}
