package org.mentpeak.test.vo;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.mentpeak.test.entity.TestTeacherOption;

/**
 * 教师评定题支信息表视图实体类
 *
 * @author lxp
 * @since 2022-08-15
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "TestTeacherOptionVO对象", description = "教师评定题支信息表")
public class TestTeacherOptionVO extends TestTeacherOption {
	private static final long serialVersionUID = 1L;

}
