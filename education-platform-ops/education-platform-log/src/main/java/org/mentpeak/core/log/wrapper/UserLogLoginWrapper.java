package org.mentpeak.core.log.wrapper;

import org.mentpeak.core.log.entity.UserLogLogin;
import org.mentpeak.core.log.vo.UserLogLoginVO;
import org.mentpeak.core.mybatisplus.support.BaseEntityWrapper;
import org.mentpeak.core.tool.utils.BeanUtil;

import lombok.AllArgsConstructor;

/**
 * 用户错误登录日志包装类,返回视图层所需的字段
 *
 * @author lxp
 * @since 2021-03-27
 */
@AllArgsConstructor
public class UserLogLoginWrapper extends BaseEntityWrapper < UserLogLogin, UserLogLoginVO > {


    @Override
    public UserLogLoginVO entityVO ( UserLogLogin userLogLogin ) {
        UserLogLoginVO userLogLoginVO = BeanUtil.copy ( userLogLogin ,
                                                        UserLogLoginVO.class );


        return userLogLoginVO;
    }

}
