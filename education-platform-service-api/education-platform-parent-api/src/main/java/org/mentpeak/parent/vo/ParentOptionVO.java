package org.mentpeak.parent.vo;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.mentpeak.parent.entity.ParentOption;

/**
 * 家长他评问卷题支信息表视图实体类
 *
 * @author lxp
 * @since 2022-06-17
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "ParentOptionVO对象", description = "家长他评问卷题支信息表")
public class ParentOptionVO extends ParentOption {
	private static final long serialVersionUID = 1L;

}
