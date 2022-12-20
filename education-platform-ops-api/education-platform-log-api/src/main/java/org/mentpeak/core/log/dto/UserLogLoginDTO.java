package org.mentpeak.core.log.dto;

import org.mentpeak.core.log.entity.UserLogLogin;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户错误登录日志数据传输对象实体类
 *
 * @author lxp
 * @since 2021-03-27
 */
@Data
@EqualsAndHashCode ( callSuper = true )
public class UserLogLoginDTO extends UserLogLogin {
    private static final long serialVersionUID = 1L;

}
