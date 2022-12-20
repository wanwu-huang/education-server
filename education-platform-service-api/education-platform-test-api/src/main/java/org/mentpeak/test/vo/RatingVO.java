package org.mentpeak.test.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author hzl
 * @data 2022年08月09日17:45
 * 评定等级
 */
@Data
public class RatingVO implements Serializable {

    @ApiModelProperty(value = "标题")
    private String title;
    @ApiModelProperty(value = "评定等级")
    private List<LevelVO> levelVOList;
}
