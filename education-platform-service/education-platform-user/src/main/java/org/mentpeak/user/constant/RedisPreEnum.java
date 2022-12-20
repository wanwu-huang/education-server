package org.mentpeak.user.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 缓存前缀
 *
 * @author lxp
 */
@Getter
@AllArgsConstructor
public enum RedisPreEnum {


    SMS_CODE_REDIS_PREFIX ( "sms_code:" ,
                            "短信缓存前缀" ),
    SMS_CODE_LOGIN ( SMS_CODE_REDIS_PREFIX.getCode ( ) + "_login:" ,
                     "短信缓存登录" ),
    SMS_CODE_REGISTERED ( SMS_CODE_REDIS_PREFIX.getCode ( ) + "_registered:" ,
                          "短信缓存注册" ),
    SMS_CODE_FORGET ( SMS_CODE_REDIS_PREFIX.getCode ( ) + "_forget:" ,
                      "短信缓存忘记密码" ),
    SMS_CODE_UPDATE ( SMS_CODE_REDIS_PREFIX.getCode ( ) + "_update:" ,
                      "短信缓存修改密码" ),
    SMS_CODE_UPDATE_PHONE ( SMS_CODE_REDIS_PREFIX.getCode ( ) + "_update_phone:" ,
            "换绑手机号" );
    private String code;

    private String desc;

}
