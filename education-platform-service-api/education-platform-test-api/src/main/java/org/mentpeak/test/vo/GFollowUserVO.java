package org.mentpeak.test.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author hzl
 * @data 2022年08月27日15:59
 * 年级关注等级分布人数
 */
@Data
public class GFollowUserVO implements Serializable {

    @ApiModelProperty(value = "参与班级")
    private String className;
    @ApiModelProperty(value = "三级")
    private List<UserInfoVO> voThreeList;
    @ApiModelProperty(value = "二级")
    private List<UserInfoVO> voTwoList;
    @ApiModelProperty(value = "一级")
    private List<UserInfoVO> voOneList;
}
