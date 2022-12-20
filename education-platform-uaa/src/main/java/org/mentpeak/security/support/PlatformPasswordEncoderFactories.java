package org.mentpeak.security.support;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;
import org.springframework.security.crypto.scrypt.SCryptPasswordEncoder;

import java.util.HashMap;
import java.util.Map;

/**
 * 自定义密码工厂
 *
 * @author mp
 */
public class PlatformPasswordEncoderFactories {

    /**
     * Creates a {@link DelegatingPasswordEncoder} with default mappings. Additional
     * mappings may be added and the encoding will be updated to conform with best
     * practices. However, due to the nature of {@link DelegatingPasswordEncoder} the
     * updates should not impact users. The mappings current are:
     *
     * <ul>
     * <li>platform - {@link PlatformPasswordEncoder} (sha1(md5("password")))</li>
     * <li>bcrypt - {@link BCryptPasswordEncoder} (Also used for encoding)</li>
     * <li>noop - {@link PlatformNoOpPasswordEncoder}</li>
     * <li>pbkdf2 - {@link Pbkdf2PasswordEncoder}</li>
     * <li>scrypt - {@link SCryptPasswordEncoder}</li>
     * </ul>
     *
     * @return the {@link PasswordEncoder} to use
     */
    @SuppressWarnings ( "deprecation" )
    public static PasswordEncoder createDelegatingPasswordEncoder ( ) {
        String encodingId = "platform";
        Map < String, PasswordEncoder > encoders = new HashMap <> ( 16 );
        encoders.put ( encodingId ,
                       new PlatformPasswordEncoder ( ) );
        encoders.put ( "bcrypt" ,
                       new BCryptPasswordEncoder ( ) );
        encoders.put ( "noop" ,
                       PlatformNoOpPasswordEncoder.getInstance ( ) );
        encoders.put ( "pbkdf2" ,
                       new Pbkdf2PasswordEncoder ( ) );
        encoders.put ( "scrypt" ,
                       new SCryptPasswordEncoder ( ) );

        return new DelegatingPasswordEncoder ( encodingId ,
                                               encoders );
    }

    private PlatformPasswordEncoderFactories ( ) {
    }

}
