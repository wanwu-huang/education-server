package org.mentpeak.test.vo;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.mentpeak.test.entity.ClassUser;

/**
 * 班级用户表视图实体类
 *
 * @author lxp
 * @since 2022-07-12
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "ClassUserVO对象", description = "班级用户表")
public class ClassUserVO extends ClassUser {
	private static final long serialVersionUID = 1L;

}
