package org.mentpeak.security.support;

import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * 无密码加密
 *
 * @author mp
 */
public class PlatformNoOpPasswordEncoder implements PasswordEncoder {

    @Override
    public String encode ( CharSequence rawPassword ) {
        return rawPassword.toString ( );
    }

    @Override
    public boolean matches (
            CharSequence rawPassword ,
            String encodedPassword ) {
        return rawPassword.toString ( )
                          .equals ( encodedPassword );
    }

    /**
     * Get the singleton {@link PlatformNoOpPasswordEncoder}.
     */
    public static PasswordEncoder getInstance ( ) {
        return INSTANCE;
    }

    private static final PasswordEncoder INSTANCE = new PlatformNoOpPasswordEncoder ( );

    private PlatformNoOpPasswordEncoder ( ) {
    }

}
