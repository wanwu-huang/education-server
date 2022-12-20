package org.mentpeak.test.vo;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.mentpeak.test.entity.GradeClass;

/**
 * 年级-班级对应关联表视图实体类
 *
 * @author lxp
 * @since 2022-08-12
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "GradeClassVO对象", description = "年级-班级对应关联表")
public class GradeClassVO extends GradeClass {
	private static final long serialVersionUID = 1L;

}
