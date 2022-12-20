package org.mentpeak.security.sms;


import org.mentpeak.core.tool.api.Result;
import org.mentpeak.core.tool.utils.Func;
import org.mentpeak.core.tool.utils.ObjectUtil;
import org.mentpeak.core.tool.utils.WebUtil;
import org.mentpeak.security.constant.AuthConstant;
import org.mentpeak.security.service.PlatformUserDetails;
import org.mentpeak.security.utils.TokenUtil;
import org.mentpeak.user.entity.UserExt;
import org.mentpeak.user.entity.UserInfo;
import org.mentpeak.user.feign.IUserClient;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

import lombok.AllArgsConstructor;

/**
 * @author MyPC
 */
@Service
@AllArgsConstructor
public class UserByPhoneDetailsService {

    private IUserClient userClient;

    public UserDetails loadUserByUsername ( String username ) {
        HttpServletRequest request = WebUtil.getRequest ( );
        String tenantCode = Func.toStr ( request.getHeader ( TokenUtil.TENANT_HEADER_KEY ) ,
                                         TokenUtil.DEFAULT_TENANT_CODE );
        Result < UserInfo > result = userClient.userInfoByPhone ( tenantCode ,
                                                                  username );
        if ( result.isSuccess ( ) ) {
            org.mentpeak.user.entity.User user = result.getData ( )
                                                       .getUser ( );
            if ( user == null ) {
                throw new UsernameNotFoundException ( TokenUtil.USER_NOT_FOUND );
            }

            UserExt userExt = userClient.userExtInfoById ( user.getId ( ) )
                                        .getData ( );
            String avatar = TokenUtil.DEFAULT_AVATAR;

            if ( ObjectUtil.isNotEmpty ( userExt ) ) {
                avatar = userExt.getHeadUrl ( );
            }

            return new PlatformUserDetails ( user.getId ( ) ,
                                             user.getTenantCode ( ) ,
                                             user.getName ( ) ,
                                             user.getRoleId ( ) ,
                                             Func.join ( result.getData ( )
                                                               .getRoles ( ) ) ,
                                             avatar ,
                                             user.getRealName ( ) ,
                                             username ,
                                             AuthConstant.ENCRYPT + user.getPassword ( ) ,
                                             true ,
                                             true ,
                                             true ,
                                             true ,
                                             AuthorityUtils.commaSeparatedStringToAuthorityList ( Func.join ( result.getData ( )
                                                                                                                    .getRoles ( ) ) ) );
        } else {
            throw new UsernameNotFoundException ( result.getMsg ( ) );
        }
    }
}
