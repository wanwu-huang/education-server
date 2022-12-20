package org.mentpeak.core.log.wrapper;

import org.mentpeak.core.log.entity.UserLogAction;
import org.mentpeak.core.log.vo.UserLogActionVO;
import org.mentpeak.core.mybatisplus.support.BaseEntityWrapper;
import org.mentpeak.core.tool.utils.BeanUtil;

import lombok.AllArgsConstructor;

/**
 * 管理后台-操作日志表包装类,返回视图层所需的字段
 *
 * @author lxp
 * @since 2021-03-27
 */
@AllArgsConstructor
public class UserLogActionWrapper extends BaseEntityWrapper < UserLogAction, UserLogActionVO > {


    @Override
    public UserLogActionVO entityVO ( UserLogAction userLogAction ) {
        UserLogActionVO userLogActionVO = BeanUtil.copy ( userLogAction ,
                                                          UserLogActionVO.class );


        return userLogActionVO;
    }

}
