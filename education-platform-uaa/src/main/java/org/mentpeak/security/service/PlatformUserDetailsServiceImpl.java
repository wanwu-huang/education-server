package org.mentpeak.security.service;

import org.mentpeak.core.log.exception.PlatformApiException;
import org.mentpeak.core.tool.api.Result;
import org.mentpeak.core.tool.utils.Func;
import org.mentpeak.core.tool.utils.ObjectUtil;
import org.mentpeak.core.tool.utils.WebUtil;
import org.mentpeak.security.constant.AuthConstant;
import org.mentpeak.security.utils.TokenUtil;
import org.mentpeak.user.entity.User;
import org.mentpeak.user.entity.UserExt;
import org.mentpeak.user.entity.UserInfo;
import org.mentpeak.user.feign.IUserClient;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;

/**
 * 用户信息
 *
 * @author mp
 */
@Service
@AllArgsConstructor
public class PlatformUserDetailsServiceImpl implements UserDetailsService {

    private IUserClient userClient;

    @Override
    @SneakyThrows
    public UserDetails loadUserByUsername ( String username ) {
        HttpServletRequest request = WebUtil.getRequest ( );
        String tenantCode = Func.toStr ( request.getHeader ( TokenUtil.TENANT_HEADER_KEY ) ,
                                         TokenUtil.DEFAULT_TENANT_CODE );
        Result < UserInfo > result = userClient.userInfoByEmail ( username );
        if ( result.isSuccess ( ) ) {
            User user = result.getData ( )
                              .getUser ( );
//            if ( user == null ) {
//                throw new UsernameNotFoundException ( TokenUtil.USER_NOT_FOUND );
//            }
            if (Func.isBlank(user.getEmail())) {
                throw new PlatformApiException("该邮箱暂未注册");
            }
            if ( user.getStatus ( ) == 0 ) {
                throw new DisabledException ( TokenUtil.ACCOUNT_DISABLED );
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
                                             user.getEmail() ,
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
