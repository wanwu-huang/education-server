package org.mentpeak.user.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.mentpeak.user.entity.Role;

/**
 * 平台-角色表数据传输对象实体类
 *
 * @author lxp
 * @since 2022-07-19
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class RoleDTO extends Role {
	private static final long serialVersionUID = 1L;

}
