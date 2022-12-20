package org.mentpeak.test.entity.mongo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.mentpeak.test.vo.FollowUserVO;
import org.mentpeak.test.vo.InvalidVO;
import org.mentpeak.test.vo.UserInfoVO;

import java.util.List;

/**
 * @author hzl
 * @data 2022年08月16日10:45
 * 年级报告mongodb
 */
@Data
public class GradeReportData {

    @ApiModelProperty(value = "ID")
    private String id;
    @ApiModelProperty(value = "年级ID")
    private Long gradeId;

    @ApiModelProperty(value = "任务ID")
    private Long taskId;

    @ApiModelProperty(value = "实际人数")
    private Integer totalPeople;
    @ApiModelProperty(value = "已测人数")
    private Integer testPeople;
    @ApiModelProperty(value = "未测人数")
    private Integer noTestPeople;
    @ApiModelProperty(value = "无效人数")
    private Integer invalidPeople;
    @ApiModelProperty(value = "完成率")
    private Double completionRate;
    @ApiModelProperty(value = "作答有效性")
    private InvalidVO invalidVO;
    @ApiModelProperty(value = "一级关注人数")
    private Integer followPeopleOne = 0;
    @ApiModelProperty(value = "一级关注占比")
    private Double followRateOne;
    @ApiModelProperty(value = "一级关注人数信息")
    private List<UserInfoVO> userInfoOneList;
    @ApiModelProperty(value = "二级关注人数")
    private Integer followPeopleTwo = 0;
    @ApiModelProperty(value = "二级关注占比")
    private Double followRateTwo;
    @ApiModelProperty(value = "二级关注人数信息")
    private List<UserInfoVO> userInfoTwoList;
    @ApiModelProperty(value = "三级关注人数")
    private Integer followPeopleThree = 0;
    @ApiModelProperty(value = "三级关注占比")
    private Double followRateThree;
    @ApiModelProperty(value = "三级关注人数信息")
    private List<UserInfoVO> userInfoThreeList;
    @ApiModelProperty(value = "良好关注人数")
    private Integer followPeople = 0;
    @ApiModelProperty(value = "良好关注占比")
    private Double followRate;

    @ApiModelProperty(value = "关注人数")
    private List<FollowUserVO> followUserList;
    @ApiModelProperty(value = "一级预警人数")
    private Integer warnPeopleOne = 0;
    @ApiModelProperty(value = "一级预警占比")
    private Double warnRateOne;
    @ApiModelProperty(value = "一级预警人数信息")
    private List<UserInfoVO> userInfoWarnOneList;
    @ApiModelProperty(value = "二级预警人数")
    private Integer warnPeopleTwo = 0;
    @ApiModelProperty(value = "二级预警占比")
    private Double warnRateTwo;
    @ApiModelProperty(value = "二级预警人数信息")
    private List<UserInfoVO> userInfoWranTwoList;
    @ApiModelProperty(value = "三级预警人数")
    private Integer warnPeopleThree = 0;
    @ApiModelProperty(value = "三级预警占比")
    private Double warnRateThree;
    @ApiModelProperty(value = "三级预警人数信息")
    private List<UserInfoVO> userInfoWranThreeList;
    @ApiModelProperty(value = "良好预警人数")
    private Integer warnPeople = 0;
    @ApiModelProperty(value = "良好预警占比")
    private Double warnRate;

    @ApiModelProperty(value = "预警人数")
    private List<FollowUserVO> warnUserList;
    @ApiModelProperty(value = "学生评定一级占比")
    private Double studentRateOne;
    @ApiModelProperty(value = "学生评定二级占比")
    private Double studentRateTwo;
    @ApiModelProperty(value = "学生评定三级占比")
    private Double studentRateThree;
    @ApiModelProperty(value = "学生评定良好占比")
    private Double studentRate;
    @ApiModelProperty(value = "教师评定一级占比")
    private Double teacherRateOne;
    @ApiModelProperty(value = "教师评定二级占比")
    private Double teacherRateTwo;
    @ApiModelProperty(value = "教师评定三级占比")
    private Double teacherRateThree;
    @ApiModelProperty(value = "教师评定三级占比")
    private Double teacherRate;

    @ApiModelProperty(value = "家长评定一级占比")
    private Double parentRateOne;
    @ApiModelProperty(value = "家长评定良好占比")
    private Double parentRate;

    /**
     * 学习状态 维度计分
     */
    private Double studyStatusScore = 0D;
    /**
     * 品行表现 维度计分
     */
    private Double behaviorScore= 0D;
    /**
     * 心理韧性 维度计分
     */
    private Double mentalToughnessScore= 0D;
    /**
     * 综合压力 维度计分
     */
    private Double overallStressScore= 0D;
    /**
     * 情绪指数 维度计分
     */
    private Double emotionalIndexScore= 0D;
    /**
     * 睡眠指数 维度计分
     */
    private Double sleepIndexScore= 0D;
    /**
     * 自杀意念 维度计分
     */
    private Double suicidalIdeationScore= 0D;
    // 指标分
    /**
     * 学习态度 指标计分
     */
    private Double learningAttitudeScore= 0D;

    private Integer learningAttitudeScoreVeryBad;
    private Integer learningAttitudeScoreBad;
    private Integer learningAttitudeScoreGenerally;
    private Integer learningAttitudeScoreGood;
    /**
     * 时间管理 指标计分
     */
    private Double timeManagementScore= 0D;

    private Integer timeManagementScoreVeryBad;
    private Integer timeManagementScoreBad;
    private Integer timeManagementScoreGenerally;
    private Integer timeManagementScoreGood;
    /**
     * 学习倦怠 指标计分
     */
    private Double learningBurnoutScore= 0D;

    private Integer learningBurnoutScoreVeryBad;
    private Integer learningBurnoutScoreBad;
    private Integer learningBurnoutScoreGenerally;
    private Integer learningBurnoutScoreGood;
    /**
     * 道德性 指标计分
     */
    private Double moralScore= 0D;

    private Integer moralScoreVeryBad;
    private Integer moralScoreBad;
    private Integer moralScoreGenerally;
    private Integer moralScoreGood;
    /**
     * 稳定性 指标计分
     */
    private Double stabilityScore= 0D;

    private Integer stabilityScoreVeryBad;
    private Integer stabilityScoreBad;
    private Integer stabilityScoreGenerally;
    private Integer stabilityScoreGood;
    /**
     * 纪律性 指标计分
     */
    private Double disciplineScore= 0D;

    private Integer disciplineScoreVeryBad;
    private Integer disciplineScoreBad;
    private Integer disciplineScoreGenerally;
    private Integer disciplineScoreGood;
    /**
     * 其他表现 指标计分
     */
    private Double otherPerformanceScore= 0D;

    private Integer otherPerformanceScoreVeryBad;
    private Integer otherPerformanceScoreBad;
    private Integer otherPerformanceScoreGenerally;
    private Integer otherPerformanceScoreGood;
    /**
     * 情绪管理 指标计分
     */
    private Double emotionManagementScore= 0D;

    private Integer emotionManagementScoreVeryBad;
    private Integer emotionManagementScoreBad;
    private Integer emotionManagementScoreGenerally;
    private Integer emotionManagementScoreGood;
    /**
     * 目标激励 指标计分
     */
    private Double goalMotivationScore= 0D;

    private Integer goalMotivationScoreVeryBad;
    private Integer goalMotivationScoreBad;
    private Integer goalMotivationScoreGenerally;
    private Integer goalMotivationScoreGood;
    /**
     * 积极关注 指标计分
     */
    private Double positiveAttentionScore= 0D;

    private Integer positiveAttentionScoreVeryBad;
    private Integer positiveAttentionScoreBad;
    private Integer positiveAttentionScoreGenerally;
    private Integer positiveAttentionScoreGood;
    /**
     * 学校支持 指标计分
     */
    private Double schoolSupportScore= 0D;

    private Integer schoolSupportScoreVeryBad;
    private Integer schoolSupportScoreBad;
    private Integer schoolSupportScoreGenerally;
    private Integer schoolSupportScoreGood;
    /**
     * 人际支持 指标计分
     */
    private Double interpersonalSupportScore= 0D;

    private Integer interpersonalSupportScoreVeryBad;
    private Integer interpersonalSupportScoreBad;
    private Integer interpersonalSupportScoreGenerally;
    private Integer interpersonalSupportScoreGood;
    /**
     * 家庭支持 指标计分
     */
    private Double familySupportScore= 0D;

    private Integer familySupportScoreVeryBad;
    private Integer familySupportScoreBad;
    private Integer familySupportScoreGenerally;
    private Integer familySupportScoreGood;
    /**
     * 学习压力 指标计分
     */
    private Double studyStressScore= 0D;

    private Integer studyStressScoreVeryBad;
    private Integer studyStressScoreBad;
    private Integer studyStressScoreGenerally;
    private Integer studyStressScoreGood;
    /**
     * 人际压力 指标计分
     */
    private Double interpersonalStressScore= 0D;

    private Integer interpersonalStressScoreVeryBad;
    private Integer interpersonalStressScoreBad;
    private Integer interpersonalStressScoreGenerally;
    private Integer interpersonalStressScoreGood;
    /**
     * 受惩罚压力 指标计分
     */
    private Double punishmentStressScore= 0D;

    private Integer punishmentStressScoreVeryBad;
    private Integer punishmentStressScoreBad;
    private Integer punishmentStressScoreGenerally;
    private Integer punishmentStressScoreGood;
    /**
     * 丧失压力 指标计分
     */
    private Double lossStressScore= 0D;

    private Integer lossStressScoreVeryBad;
    private Integer lossStressScoreBad;
    private Integer lossStressScoreGenerally;
    private Integer lossStressScoreGood;
    /**
     * 适应压力 指标计分
     */
    private Double adaptationStressScore= 0D;

    private Integer adaptationStressScoreVeryBad;
    private Integer adaptationStressScoreBad;
    private Integer adaptationStressScoreGenerally;
    private Integer adaptationStressScoreGood;
    /**
     * 强迫 指标计分
     */
    private Double compulsionScore= 0D;

    private Integer compulsionScoreVeryBad;
    private Integer compulsionScoreBad;
    private Integer compulsionScoreGenerally;
    private Integer compulsionScoreGood;
    /**
     * 人偏执 指标计分
     */
    private Double paranoiaScore= 0D;

    private Integer paranoiaScoreVeryBad;
    private Integer paranoiaScoreBad;
    private Integer paranoiaScoreGenerally;
    private Integer paranoiaScoreGood;
    /**
     * 敌对 指标计分
     */
    private Double hostilityScore= 0D;

    private Integer hostilityScoreVeryBad;
    private Integer hostilityScoreBad;
    private Integer hostilityScoreGenerally;
    private Integer hostilityScoreGood;
    /**
     * 人际敏感 指标计分
     */
    private Double interpersonalSensitivityScore= 0D;

    private Integer interpersonalSensitivityScoreVeryBad;
    private Integer interpersonalSensitivityScoreBad;
    private Integer interpersonalSensitivityScoreGenerally;
    private Integer interpersonalSensitivityScoreGood;
    /**
     * 焦虑 指标计分
     */
    private Double anxietyScore= 0D;

    private Integer anxietyScoreVeryBad;
    private Integer anxietyScoreBad;
    private Integer anxietyScoreGenerally;
    private Integer anxietyScoreGood;
    /**
     * 抑郁 指标计分
     */
    private Double depressionScore= 0D;

    private Integer depressionScoreVeryBad;
    private Integer depressionScoreBad;
    private Integer depressionScoreGenerally;
    private Integer depressionScoreGood;

    /**
     * 睡眠指数
     */
    private Integer sleepVeryBad;
    private Integer sleepBad;
    private Integer sleepGenerally;
    private Integer sleepGood;
}
