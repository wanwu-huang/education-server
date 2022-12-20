
package org.mentpeak.dict.feign;

import org.mentpeak.core.tool.api.Result;
import org.mentpeak.dict.entity.Dict;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Feign失败配置
 *
 * @author lxp
 */
@Component
public class IDictClientFallback implements IDictClient {
    @Override
    public Result < String > getValue (
            String code ,
            Integer dictKey ) {
        return Result.fail ( "获取数据失败" );
    }

    @Override
    public Result < List < Dict > > getList ( String code ) {
        return Result.fail ( "获取数据失败" );
    }
}
