package org.mentpeak.user.feign;

import org.mentpeak.core.launch.constant.AppConstant;
import org.mentpeak.core.tool.api.Result;
import org.mentpeak.user.entity.SysProvince;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient (
        value = AppConstant.APPLICATION_USER_NAME,
        fallback = IAreaClientFallback.class
)
public interface IAreaClient {

    String API_PREFIX = "/userext";
    String GET_PROVINCE = API_PREFIX + "/getProvinceInfo";

    /**
     * 根据市编码查询省份信息
     *
     * @param cityCode
     * @return
     */
    @GetMapping ( GET_PROVINCE )
    Result < SysProvince > getProvinceInfo ( @RequestParam ( "cityCode" ) String cityCode );
}
