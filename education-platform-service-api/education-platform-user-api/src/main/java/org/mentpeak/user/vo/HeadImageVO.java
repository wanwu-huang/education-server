package org.mentpeak.user.vo;


import java.io.Serializable;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 视图实体类
 *
 * @author mp
 */
@Data
@ApiModel ( value = "HeadImageVO对象", description = "HeadImageVOUserInfoVO对象" )
public class HeadImageVO implements Serializable {
    private static final long serialVersionUID = 1L;
    @ApiModelProperty ( "用户头像" )
    private String headUrl;
}
