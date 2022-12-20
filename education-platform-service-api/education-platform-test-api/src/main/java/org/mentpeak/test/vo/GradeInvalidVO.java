package org.mentpeak.test.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author hzl
 * @data 2022年08月09日17:18
 * 年级-作答有效性
 */
@Data
public class GradeInvalidVO implements Serializable {

    @ApiModelProperty(value = "无效人数")
    private Integer totalInvalidPeople;
    @ApiModelProperty(value = "无效人数名单")
    private List<GInvalidVO> gInvalidVOList = new ArrayList<>();
}
