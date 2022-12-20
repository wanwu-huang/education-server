package org.mentpeak.test.vo;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.mentpeak.test.entity.TestTeacherQuestion;

/**
 * 教师评定题干信息表视图实体类
 *
 * @author lxp
 * @since 2022-08-15
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "TestTeacherQuestionVO对象", description = "教师评定题干信息表")
public class TestTeacherQuestionVO extends TestTeacherQuestion {
	private static final long serialVersionUID = 1L;

}
