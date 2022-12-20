package org.mentpeak.user.vo;

import org.mentpeak.user.entity.UserExt;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户信息扩展表视图实体类
 *
 * @author lxp
 * @since 2021-03-27
 */
@Data
@EqualsAndHashCode ( callSuper = true )
@ApiModel ( value = "UserExtVO对象", description = "用户信息扩展表" )
public class UserExtVO extends UserExt {
    private static final long serialVersionUID = 1L;

}
