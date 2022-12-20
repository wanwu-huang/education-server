package org.mentpeak.core.log.dto;

import org.mentpeak.core.log.entity.UserLogAction;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 管理后台-操作日志表数据传输对象实体类
 *
 * @author lxp
 * @since 2021-03-27
 */
@Data
@EqualsAndHashCode ( callSuper = true )
public class UserLogActionDTO extends UserLogAction {
    private static final long serialVersionUID = 1L;

}
