package org.mentpeak.test.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.mentpeak.core.mybatisplus.base.BaseEntity;

/**
 * 关注等级
实体类
 *
 * @author lxp
 * @since 2022-07-12
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "TestFollowLevel对象", description = "关注等级")
public class TestFollowLevel extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 等级名字
     */
    @ApiModelProperty(value = "等级名字")
    private String name;
    /**
     * 风险指数 0 良好  1 1级关注  2  2级关注  3  3级关注
     */
    @ApiModelProperty(value = "风险指数 0 良好  1 1级关注  2  2级关注  3  3级关注")
    private Integer riskIndex;


}
