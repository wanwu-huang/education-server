package org.mentpeak.test.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.mentpeak.test.entity.TestTeacherOption;

/**
 * 教师评定题支信息表数据传输对象实体类
 *
 * @author lxp
 * @since 2022-08-15
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class TestTeacherOptionDTO extends TestTeacherOption {
	private static final long serialVersionUID = 1L;

}
