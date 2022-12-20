package org.mentpeak.test.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import io.swagger.annotations.ApiModel;
import org.mentpeak.test.entity.TestPaperQuestion;

/**
 * 试卷和题干及选中题支关系表视图实体类
 *
 * @author lxp
 * @since 2022-07-15
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "TestPaperQuestionVO对象", description = "试卷和题干及选中题支关系表")
public class TestPaperQuestionVO extends TestPaperQuestion {
	private static final long serialVersionUID = 1L;

}
