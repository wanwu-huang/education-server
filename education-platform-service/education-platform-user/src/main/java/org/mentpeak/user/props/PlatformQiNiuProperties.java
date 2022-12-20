package org.mentpeak.user.props;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

/**
 * 异步配置
 *
 * @author lxp
 */
@Getter
@Setter
@RefreshScope
@Component
@ConfigurationProperties ( "platform.qiniu" )
public class PlatformQiNiuProperties {
    // accessKey
    private String accessKey = "access key";
    // secretKey
    private String secretKey = "secret key";
    // public Bucket
    private String publicBucket = "bucket name";
    // private Bucket
    private String privateBucket = "privateBucket";
    // 回调地址
    private String callbackURL = "callBackURL";
    //publicIMGURL
    private String publicIMGURL = "IMGURL";
    // priviteIMGURL
    private String priviteIMGURL = "priviteIMGURL";
}
