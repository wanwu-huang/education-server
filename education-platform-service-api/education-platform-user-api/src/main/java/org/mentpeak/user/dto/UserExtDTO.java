package org.mentpeak.user.dto;

import org.mentpeak.user.entity.UserExt;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户信息扩展表数据传输对象实体类
 *
 * @author lxp
 * @since 2021-03-27
 */
@Data
@EqualsAndHashCode ( callSuper = true )
public class UserExtDTO extends UserExt {
    private static final long serialVersionUID = 1L;

}
