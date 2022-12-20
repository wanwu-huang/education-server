package org.mentpeak.test.vo;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.mentpeak.test.entity.TestTeacherConclusion;

/**
 * 教师他评结论视图实体类
 *
 * @author lxp
 * @since 2022-07-12
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "TestTeacherConclusionVO对象", description = "教师他评结论")
public class TestTeacherConclusionVO extends TestTeacherConclusion {
	private static final long serialVersionUID = 1L;

}
