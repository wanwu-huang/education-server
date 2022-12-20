
package org.mentpeak.security.constant;

/**
 * 授权校验常量
 *
 * @author mp
 */
public interface AuthConstant {

    /**
     * 密码加密规则
     */
    String ENCRYPT = "{platform}";

    /**
     * platform_client表字段
     */
    String CLIENT_FIELDS = "client_id, CONCAT('{noop}',client_secret) as client_secret, resource_ids, scope, authorized_grant_types, " +
            "web_server_redirect_uri, authorities, access_token_validity, " +
            "refresh_token_validity, additional_information, autoapprove";

    /**
     * platform_client查询语句
     */
    String BASE_STATEMENT = "select " + CLIENT_FIELDS + " from platform_client";

    /**
     * platform_client查询排序
     */
    String DEFAULT_FIND_STATEMENT = BASE_STATEMENT + " order by client_id";

    /**
     * 查询client_id
     */
    String DEFAULT_SELECT_STATEMENT = BASE_STATEMENT + " where client_id = ?";


    /**
     * sms验证码，redis key
     */
    String SMS_CODE_REDIS_PREFIX = "sms_code:";


    /**
     * 自定义client表名
     */
    public static final String CLIENT_TABLE = "platform_client";

    /**
     * 客户端模式
     */
    public static final String CLIENT_CREDENTIALS = "client_credentials";
}
