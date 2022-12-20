package org.mentpeak.test.vo;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.mentpeak.test.entity.TestTeacherPaper;

/**
 * 教师评定试卷信息表视图实体类
 *
 * @author lxp
 * @since 2022-08-15
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "TestTeacherPaperVO对象", description = "教师评定试卷信息表")
public class TestTeacherPaperVO extends TestTeacherPaper {
	private static final long serialVersionUID = 1L;

}
