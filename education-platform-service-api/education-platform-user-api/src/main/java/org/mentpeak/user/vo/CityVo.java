package org.mentpeak.user.vo;/**
 * @author hzl
 * @create 2021-04-09
 */

import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
 * @author hzl
 * @data 2021年04月09日18:28
 */
@Data
@ApiModel ( value = "CityVo对象", description = "CityVo对象" )
public class CityVo {
    // 市编码
    private String value;
    // 市名称
    private String label;
}
