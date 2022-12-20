package org.mentpeak.core.log.vo;

import org.mentpeak.core.log.entity.UserLogAction;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 管理后台-操作日志表视图实体类
 *
 * @author lxp
 * @since 2021-03-27
 */
@Data
@EqualsAndHashCode ( callSuper = true )
@ApiModel ( value = "UserLogActionVO对象", description = "管理后台-操作日志表" )
public class UserLogActionVO extends UserLogAction {
    private static final long serialVersionUID = 1L;

}
