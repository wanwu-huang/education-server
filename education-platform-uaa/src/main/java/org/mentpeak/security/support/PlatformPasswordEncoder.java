package org.mentpeak.security.support;

import org.mentpeak.core.tool.utils.DigestUtil;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * 自定义密码加密
 *
 * @author mp
 */
public class PlatformPasswordEncoder implements PasswordEncoder {

    @Override
    public String encode ( CharSequence rawPassword ) {
        return DigestUtil.encrypt ( ( String ) rawPassword );
    }

    @Override
    public boolean matches (
            CharSequence rawPassword ,
            String encodedPassword ) {
        return encodedPassword.equals ( encode ( rawPassword ) );
    }


}
