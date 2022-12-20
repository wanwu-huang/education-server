package org.mentpeak.user.wrapper;

import org.mentpeak.core.mybatisplus.support.BaseEntityWrapper;
import org.mentpeak.core.tool.utils.BeanUtil;
import org.mentpeak.user.entity.UserExt;
import org.mentpeak.user.vo.UserExtVO;

import lombok.AllArgsConstructor;

/**
 * 用户信息扩展表包装类,返回视图层所需的字段
 *
 * @author lxp
 * @since 2021-03-27
 */
@AllArgsConstructor
public class UserExtWrapper extends BaseEntityWrapper < UserExt, UserExtVO > {


    @Override
    public UserExtVO entityVO ( UserExt userExt ) {
        UserExtVO userExtVO = BeanUtil.copy ( userExt ,
                                              UserExtVO.class );


        return userExtVO;
    }

}
