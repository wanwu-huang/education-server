package org.mentpeak.test.entity.mongo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 个人报告数据
 * @author demain_lee
 * @since 2022-08-09
 */
@Data
@Builder
public class PersonalReport {

    /**
     * 主键
     */
    private String id;

    @ApiModelProperty(value = "个人信息")
    private UserInfo userInfo;

    @ApiModelProperty(value = "作答有效性")
    private String answerValidity;

    @ApiModelProperty(value = "作答有效性  0 否  1 是")
    private Integer answerIsValidity;

    @ApiModelProperty(value = "心理健康评级")
    private MentalHealthRatings mentalHealthRatings;

    @ApiModelProperty(value = "测评概况")
    private TestOverview testOverview;

    @ApiModelProperty(value = "学习状态")
    private ResultData learningStatus;

    @ApiModelProperty(value = "品行表现")
    private ResultData behavior;

    @ApiModelProperty(value = "心理韧性")
    private ResultData mentalToughness;

    @ApiModelProperty(value = "压力指数")
    private ResultData stressIndex;

    @ApiModelProperty(value = "情绪指数")
    private ResultData emotionalIndex;

    @ApiModelProperty(value = "睡眠指数")
    private ResultData sleepIndex;

    @ApiModelProperty(value = "测试时间")
    private String testTime;

    @ApiModelProperty(value = "创建时间")
    private String createTime;

    @ApiModelProperty(value = "是否有权限 0：没有 1：有")
    private Integer isPermission;

    /**
     * 个人信息
     */
    @Data
    public static class UserInfo {

        @ApiModelProperty(value = "个人信息")
        private Long userId;

        @ApiModelProperty(value = "姓名")
        private String name;

        @ApiModelProperty(value = "学籍号")
        private String studentNo;

        @ApiModelProperty(value = "性别")
        private String sex;

        @ApiModelProperty(value = "学校")
        private String school;

        @ApiModelProperty(value = "年级")
        private String gradeName;

        @ApiModelProperty(value = "班级")
        private String className;

        @ApiModelProperty(value = "家庭结构")
        private String familyStructures;

        @ApiModelProperty(value = "是否与父母生活")
        private String whetherLivingWithParents;

    }


    /**
     * 心理健康评级
     */
    @Data
    public static class MentalHealthRatings {

        @ApiModelProperty(value = "建议关注等级")
        private LevelData recommendedAttentionLevel;

        @ApiModelProperty(value = "学生评定等级")
        private LevelData studentRatingLevel;

        @ApiModelProperty(value = "教师评定等级")
        private LevelData teachRatingLevel;

        @ApiModelProperty(value = "家长评定等级")
        private LevelData parentsRatingLevel;

        @ApiModelProperty(value = "心理危机预警")
        private LevelData psychologicalCrisisAlert;

    }

    /**
     * 评定等级
     */
    @Data
    public static class LevelData {

        @ApiModelProperty(value = "等级")
        private Integer riskIndex;

        @ApiModelProperty(value = "结果")
        private String result;

        @ApiModelProperty(value = "字体颜色")
        private String fontColor;

    }


    /**
     * 测评概况
     */
    @Data
    public static class TestOverview{

        @ApiModelProperty(value = "积极")
        private List<OverviewData> active = new ArrayList<>();

        @ApiModelProperty(value = "消极")
        private List<OverviewData> negative = new ArrayList<>();
    }

    /**
     * 概况数据
     */
    @Data
    public static class OverviewData {

        @ApiModelProperty(value = "名称")
        private String title;

        @ApiModelProperty(value = "结果")
        private String result;

        @ApiModelProperty(value = "分数")
        private String score;

        @ApiModelProperty(value = "字体颜色")
        private String fontColor;

    }


    /**
     * 结果数据
     */
    @Data
    public static class ResultData{

        @ApiModelProperty(value = "总体结果")
        private TotalResult totalResult;

        @ApiModelProperty(value = "图标数据")
        private List<ChartData> chartData;
    }


    /**
     * 总体结果
     */
    @Data
    public static class TotalResult{

        @ApiModelProperty(value = "标题")
        private String title;

        @ApiModelProperty(value = "结果")
        private String result;

        @ApiModelProperty(value = "等级")
        private Integer riskIndex;

        @ApiModelProperty(value = "分数")
        private String score;

        @ApiModelProperty(value = "描述")
        private String resultDes;

        @ApiModelProperty(value = "字体颜色")
        private String fontColor;

    }


    /**
     * 图标数据
     */
    @Data
    public static class ChartData {

        @ApiModelProperty(value = "标题")
        private String title;

        @ApiModelProperty(value = "风险指数")
        private Integer riskIndex;

        @ApiModelProperty(value = "结果")
        private String result;

        @ApiModelProperty(value = "分数")
        private String score;

        @ApiModelProperty(value = "描述")
        private String resultDes;

        @ApiModelProperty(value = "字体颜色")
        private String fontColor;

        @ApiModelProperty(value = "图标颜色")
        private String chartColor;
    }



}
