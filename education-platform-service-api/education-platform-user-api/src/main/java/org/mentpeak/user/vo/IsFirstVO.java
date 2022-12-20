package org.mentpeak.user.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author hzl
 * @data 2022年09月01日10:08
 */
@Data
public class IsFirstVO implements Serializable {

    @ApiModelProperty(value = "是否首次登录 1是 0否")
    private Integer isFirst;
}
