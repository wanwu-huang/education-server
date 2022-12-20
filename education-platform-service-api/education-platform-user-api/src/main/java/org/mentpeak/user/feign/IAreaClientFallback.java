package org.mentpeak.user.feign;

import org.mentpeak.core.tool.api.Result;
import org.mentpeak.user.entity.SysProvince;
import org.springframework.stereotype.Component;

/**
 * 用户远程调用失败，降级
 */
@Component
public class IAreaClientFallback implements IAreaClient {


    @Override
    public Result < SysProvince > getProvinceInfo ( String cityCode ) {
        return Result.fail ( "根据市编码查询省份信息失败" );
    }
}
