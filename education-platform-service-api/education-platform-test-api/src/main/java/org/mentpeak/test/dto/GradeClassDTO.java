package org.mentpeak.test.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.mentpeak.test.entity.GradeClass;

/**
 * 年级-班级对应关联表数据传输对象实体类
 *
 * @author lxp
 * @since 2022-08-12
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class GradeClassDTO extends GradeClass {
	private static final long serialVersionUID = 1L;

}
