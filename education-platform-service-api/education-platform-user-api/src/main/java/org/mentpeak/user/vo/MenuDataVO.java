package org.mentpeak.user.vo;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.mentpeak.user.entity.MenuData;

/**
 * 菜单数据关联表视图实体类
 *
 * @author lxp
 * @since 2022-07-12
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "MenuDataVO对象", description = "菜单数据关联表")
public class MenuDataVO extends MenuData {
	private static final long serialVersionUID = 1L;

}
