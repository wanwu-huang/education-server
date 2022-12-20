package org.mentpeak.user.wrapper;

import org.mentpeak.core.mybatisplus.support.BaseEntityWrapper;
import org.mentpeak.core.tool.api.Result;
import org.mentpeak.core.tool.utils.BeanUtil;
import org.mentpeak.core.tool.utils.Func;
import org.mentpeak.dict.feign.IDictClient;
import org.mentpeak.user.entity.User;
import org.mentpeak.user.service.IUserService;
import org.mentpeak.user.vo.UserVO;

import java.util.List;

import lombok.AllArgsConstructor;

/**
 * 包装类,返回视图层所需的字段
 *
 * @author mp
 */
@AllArgsConstructor
public class UserWrapper extends BaseEntityWrapper < User, UserVO > {

    private IUserService userService;

    private IDictClient dictClient;

    public UserWrapper ( ) {
    }

    @Override
    public UserVO entityVO ( User user ) {
        UserVO userVO = BeanUtil.copy ( user ,
                                        UserVO.class );
        List < String > roleName = userService.getRoleName ( user.getRoleId ( ) );
        List < String > deptName = userService.getDeptName ( user.getDeptId ( ) );
        userVO.setRoleName ( Func.join ( roleName ) );
        userVO.setDeptName ( Func.join ( deptName ) );
        Result < String > dict = dictClient.getValue ( "sex" ,
                                                       Func.toInt ( user.getSex ( ) ) );
        if ( dict.isSuccess ( ) ) {
            userVO.setSexName ( dict.getData ( ) );
        }
        return userVO;
    }

}
