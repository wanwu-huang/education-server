package org.mentpeak.test.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.mentpeak.test.entity.mongo.GroupsReport;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author hzl
 * @data 2022年08月11日15:09
 * 年级心理健康评级
 */
@Data
public class GTestModuleTwoVO implements Serializable {

    @ApiModelProperty(value = "建议关注等级")
    private List<FollowLevelVO> attentionLevelTable = new ArrayList<>();
    @ApiModelProperty(value = "建议关注等级图表数据")
    private List<TypeData> attentionLevel = new ArrayList<>();
    @ApiModelProperty(value = "建议关注等级分布")
    private List<GFollowVO> attentionLevelDistribution = new ArrayList<>();
    @ApiModelProperty(value = "关注人数")
    private List<GFollowUserVO> attentionStudent = new ArrayList<>();
    @ApiModelProperty(value = "预警等级")
    private List<FollowLevelVO> warningLevelTable = new ArrayList<>();
    @ApiModelProperty(value = "建议预警等级图表数据")
    private List<TypeData> warningLevel = new ArrayList<>();
    @ApiModelProperty(value = "建议预警等级分布")
    private List<GFollowVO> warningLevelDistribution = new ArrayList<>();
    @ApiModelProperty(value = "预警人数")
    private List<GFollowUserVO> warningStudent = new ArrayList<>();


    @Data
    public static class TypeData {

        @ApiModelProperty(value = "类别名字")
        private String title;

        @ApiModelProperty(value = "图标数据")
        private List<ChartData> chartData = new ArrayList<>();

    }

    @Data
    public static class ChartData {

        @ApiModelProperty(value = "名字")
        private String name;

        @ApiModelProperty(value = "值")
        private Double value;

        @ApiModelProperty(value = "图表颜色")
        private String chartColor;

    }
}
