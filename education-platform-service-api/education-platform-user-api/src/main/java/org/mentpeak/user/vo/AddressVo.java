package org.mentpeak.user.vo;/**
 * @author hzl
 * @create 2021-04-09
 */

import java.io.Serializable;
import java.util.List;

import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
 * @author hzl
 * @data 2021年04月09日9:46
 */
@Data
@ApiModel ( value = "AddressVo对象", description = "AddressVo对象" )
public class AddressVo implements Serializable {
    private static final long serialVersionUID = 1L;
    // 省编码
    private String value;
    // 省名称
    private String label;
    private List < CityVo > children;
}
