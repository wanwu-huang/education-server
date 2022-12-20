package org.mentpeak.user.vo;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.mentpeak.user.entity.Role;

/**
 * 平台-角色表视图实体类
 *
 * @author lxp
 * @since 2022-07-19
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "RoleVO对象", description = "平台-角色表")
public class RoleVO extends Role {
	private static final long serialVersionUID = 1L;

}
