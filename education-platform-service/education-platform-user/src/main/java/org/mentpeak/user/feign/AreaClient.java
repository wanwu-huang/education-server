package org.mentpeak.user.feign;


import org.mentpeak.core.tool.api.Result;
import org.mentpeak.user.entity.SysProvince;
import org.mentpeak.user.feign.IAreaClient;
import org.mentpeak.user.service.IUserExtService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;

/**
 * 地区服务Feign实现类
 *
 * @author mp
 */
@RestController
@AllArgsConstructor
public class AreaClient implements IAreaClient {


    IUserExtService userExtService;


    @Override
    @GetMapping ( GET_PROVINCE )
    public Result < SysProvince > getProvinceInfo ( String cityCode ) {
        return Result.data ( userExtService.getProvince ( cityCode ) );
    }
}
