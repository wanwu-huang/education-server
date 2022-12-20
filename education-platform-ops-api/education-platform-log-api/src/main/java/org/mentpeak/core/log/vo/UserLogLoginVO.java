package org.mentpeak.core.log.vo;

import org.mentpeak.core.log.entity.UserLogLogin;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户错误登录日志视图实体类
 *
 * @author lxp
 * @since 2021-03-27
 */
@Data
@EqualsAndHashCode ( callSuper = true )
@ApiModel ( value = "UserLogLoginVO对象", description = "用户错误登录日志" )
public class UserLogLoginVO extends UserLogLogin {
    private static final long serialVersionUID = 1L;

}
