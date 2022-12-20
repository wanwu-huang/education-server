package org.mentpeak.user.exception;

import org.mentpeak.core.tool.api.IResultCode;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserResultCode implements IResultCode {
    /**
     * 返回参数枚举字段
     */
    USERNAME_OR_PASSWORD_ERROR ( 400 ,
                                 "用户名或密码错误!" ),
    PHONE_ERROR ( 400 ,
                  "手机号格式错误！" ),
    PHONE_NULL_ERROR ( 400 ,
                       "手机号未注册用户!" ),
    PHONE_EXIST_ERROR ( 400 ,
                        "该手机号已经注册!" ),
    SCHOOL_NULL_ERROR ( 400 ,
                        "学生未绑定学校!" ),
    PHONE_ISNULL_SUCCESS ( 400 ,
                           "手机号码不能为空!" ),
    EMAIL_ISNULL_ERROR(400,"邮箱号不能为空"),
    EMAIL_REGISTED_ERROR(4001,"该邮箱号已存在"),
    EMAIL_NULL_ERROR(4002,"该邮箱号不存在"),
    CODE_ISNULL_SUCCESS ( 400 ,
                          "验证码不能为空!" ),
    CODE_ISError_SUCCESS ( 400 ,
                           "验证码错误!" ),
    CODE_ISError_EXPIRED ( 400 ,
                           "验证码不存在或已过期！" ),
    USER_ISNOTNULL_SUCCESS ( 400 ,
                             "当前用户已存在!" ),
    PAPER_ISNULL_SUCCESS ( 400 ,
                           "学籍号不能为空!" ),
    PASSWORD_ISNULL_SUCCESS ( 400 ,
                              "密码不能为空!" ),
    PHONE_IS_BOUND ( 400 ,
            "该手机号已被绑定，请更换其他手机号!" ),
    OLD_PASSWORD_IS_ERROR ( 400 ,
            "原密码错误!" ),
    ACCOUNT_IS_EXIST ( 400 ,
            "账号不存在!" ),
    ACCOUNT_IS_DISABLE ( 400 ,
            "账号已禁用!" ),
    ;

    final int code;
    final String message;

    /**
     * 获取code
     *
     * @return
     */
    @Override
    public int getCode ( ) {
        return this.code;
    }

    /**
     * 获取message
     *
     * @return
     */
    @Override
    public String getMessage ( ) {
        return this.message;
    }

}
