package org.mentpeak.security.service;

import org.mentpeak.core.tool.utils.ObjectUtil;
import org.mentpeak.security.constant.AuthConstant;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.oauth2.common.exceptions.InvalidClientException;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.client.JdbcClientDetailsService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.sql.DataSource;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * 客户端信息
 *
 * @author mp
 */
@Slf4j
@Setter
@Service
public class PlatformClientDetailsServiceImpl extends JdbcClientDetailsService {

    @Resource
    private DataSource dataSource;

    @Resource
    private RedisTemplate < String, Object > redisTemplate;

    public PlatformClientDetailsServiceImpl ( DataSource dataSource ) {
        super ( dataSource );
        super.setSelectClientDetailsSql ( AuthConstant.DEFAULT_SELECT_STATEMENT );
        super.setFindClientDetailsSql ( AuthConstant.DEFAULT_FIND_STATEMENT );
    }


    /**
     * 从redis里读取ClientDetails
     *
     * @param clientId 客户端ID
     * @return ClientDetails
     * @throws InvalidClientException 非法客户端异常
     */
    @Override
    public ClientDetails loadClientByClientId ( String clientId ) throws InvalidClientException {
        ClientDetails clientDetails = ( ClientDetails ) redisTemplate.opsForValue ( )
                                                                     .get ( clientKey ( clientId ) );
        if ( ObjectUtil.isEmpty ( clientDetails ) ) {
            clientDetails = getCacheClient ( clientId );
        }
        clientDetails.getAuthorizedGrantTypes ( )
                     .add ( AuthConstant.CLIENT_CREDENTIALS );
        return clientDetails;
    }


    /**
     * 自定义语句查询，并将数据同步至redis
     *
     * @param clientId 客户端ID
     * @return ClientDetails
     */
    private ClientDetails getCacheClient ( String clientId ) {
        ClientDetails clientDetails = null;

        try {
            clientDetails = super.loadClientByClientId ( clientId );
            if ( ObjectUtil.isNotEmpty ( clientDetails ) ) {
                redisTemplate.opsForValue ( )
                             .set ( clientKey ( clientId ) ,
                                    clientDetails );
                log.debug ( "Cache clientId:{}, clientDetails:{}" ,
                            clientId ,
                            clientDetails );
            }
        } catch ( Exception e ) {
            log.error ( "Exception for clientId:{}, message:{}" ,
                        clientId ,
                        e.getMessage ( ) );
        }
        return clientDetails;
    }

    private String clientKey ( String clientId ) {
        return AuthConstant.CLIENT_TABLE + ":" + clientId;
    }

}
