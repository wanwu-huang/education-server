package org.mentpeak.test.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author hzl
 * @data 2022年08月11日15:09
 */
@Data
public class TestModuleTwoVO implements Serializable {

    @ApiModelProperty(value = "建议关注等级")
    private List<FollowLevelVO> attentionLevelTable = new ArrayList<>();
    @ApiModelProperty(value = "建议关注等级图表数据")
    private List<GTestModuleTwoVO.TypeData> attentionLevel = new ArrayList<>();
    @ApiModelProperty(value = "关注人数")
    private List<FollowUser> attentionStudent = new ArrayList<>();

    @ApiModelProperty(value = "预警等级")
    private List<FollowLevelVO> warningLevelTable = new ArrayList<>();
    @ApiModelProperty(value = "建议预警等级图表数据")
    private List<GTestModuleTwoVO.TypeData> warningLevel = new ArrayList<>();
    @ApiModelProperty(value = "预警人数")
    private List<FollowUser> warningStudent = new ArrayList<>();

    @Data
    public static class FollowUser {
        @ApiModelProperty(value = "3级关注/预警")
        private List<UserInfoVO> voThreeList;
        @ApiModelProperty(value = "2级关注/预警")
        private List<UserInfoVO> voTwoList;
        @ApiModelProperty(value = "1级关注/预警")
        private List<UserInfoVO> voOneList;
    }
}
