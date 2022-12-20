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
 * 班级报告mongodb
 */
@Data
public class ClassReportData {

    @ApiModelProperty(value = "ID")
    private String id;
    @ApiModelProperty(value = "年级ID")
    private Long gradeId;

    @ApiModelProperty(value = "班级ID")
    private Long classId;

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
    private Double completionRate = 0D;
    @ApiModelProperty(value = "作答有效性")
    private InvalidVO invalidVO;
    @ApiModelProperty(value = "一级关注人数")
    private Integer followPeopleOne = 0;
    @ApiModelProperty(value = "一级关注占比")
    private Double followRateOne = 0D;
    @ApiModelProperty(value = "一级关注人数信息")
    private List<UserInfoVO> userInfoOneList;
    @ApiModelProperty(value = "二级关注人数")
    private Integer followPeopleTwo = 0;
    @ApiModelProperty(value = "二级关注占比")
    private Double followRateTwo = 0D;
    @ApiModelProperty(value = "二级关注人数信息")
    private List<UserInfoVO> userInfoTwoList;
    @ApiModelProperty(value = "三级关注人数")
    private Integer followPeopleThree = 0;
    @ApiModelProperty(value = "三级关注占比")
    private Double followRateThree= 0D;
    @ApiModelProperty(value = "三级关注人数信息")
    private List<UserInfoVO> userInfoThreeList;
    @ApiModelProperty(value = "良好关注人数")
    private Integer followPeople = 0;
    @ApiModelProperty(value = "良好关注占比")
    private Double followRate = 0D;

    @ApiModelProperty(value = "关注人数")
    private List<FollowUserVO> followUserList;
    @ApiModelProperty(value = "一级预警人数")
    private Integer warnPeopleOne = 0;
    @ApiModelProperty(value = "一级预警占比")
    private Double warnRateOne= 0D;
    @ApiModelProperty(value = "一级预警人数信息")
    private List<UserInfoVO> userInfoWarnOneList;
    @ApiModelProperty(value = "二级预警人数")
    private Integer warnPeopleTwo = 0;
    @ApiModelProperty(value = "二级预警占比")
    private Double warnRateTwo= 0D;
    @ApiModelProperty(value = "二级预警人数信息")
    private List<UserInfoVO> userInfoWranTwoList;
    @ApiModelProperty(value = "三级预警人数")
    private Integer warnPeopleThree = 0;
    @ApiModelProperty(value = "三级预警占比")
    private Double warnRateThree= 0D;
    @ApiModelProperty(value = "三级预警人数信息")
    private List<UserInfoVO> userInfoWranThreeList;
    @ApiModelProperty(value = "良好预警人数")
    private Integer warnPeople = 0;
    @ApiModelProperty(value = "良好预警占比")
    private Double warnRate = 0D;

    @ApiModelProperty(value = "预警人数")
    private List<FollowUserVO> warnUserList;
    @ApiModelProperty(value = "学生评定一级占比")
    private Double studentRateOne= 0D;
    @ApiModelProperty(value = "学生评定二级占比")
    private Double studentRateTwo= 0D;
    @ApiModelProperty(value = "学生评定三级占比")
    private Double studentRateThree= 0D;
    @ApiModelProperty(value = "学生评定良好占比")
    private Double studentRate= 0D;
    @ApiModelProperty(value = "教师评定一级占比")
    private Double teacherRateOne= 0D;
    @ApiModelProperty(value = "教师评定二级占比")
    private Double teacherRateTwo= 0D;
    @ApiModelProperty(value = "教师评定三级占比")
    private Double teacherRateThree= 0D;
    @ApiModelProperty(value = "教师评定三级占比")
    private Double teacherRate= 0D;

    @ApiModelProperty(value = "家长评定一级占比")
    private Double parentRateOne= 0D;
    @ApiModelProperty(value = "家长评定良好占比")
    private Double parentRate= 0D;

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
    @ApiModelProperty(value = "学习态度差异性")
    private Double learningAttitudeScoreDiff= 0D;
    @ApiModelProperty(value = "学习态度很差人数")
    private Integer learningAttitudeScoreVeryBad= 0;
    @ApiModelProperty(value = "学习态度较差人数")
    private Integer learningAttitudeScoreBad= 0;
    @ApiModelProperty(value = "学习态度一般人数")
    private Integer learningAttitudeScoreGenerally= 0;
    @ApiModelProperty(value = "学习态度良好")
    private Integer learningAttitudeScoreGood= 0;
    /**
     * 时间管理 指标计分
     */
    private Double timeManagementScore= 0D;
    private Double timeManagementScoreDiff= 0D;
    private Integer timeManagementScoreVeryBad= 0;
    private Integer timeManagementScoreBad= 0;
    private Integer timeManagementScoreGenerally= 0;
    private Integer timeManagementScoreGood= 0;
    /**
     * 学习倦怠 指标计分
     */
    private Double learningBurnoutScore= 0D;
    private Double learningBurnoutScoreDiff= 0D;
    private Integer learningBurnoutScoreVeryBad= 0;
    private Integer learningBurnoutScoreBad= 0;
    private Integer learningBurnoutScoreGenerally= 0;
    private Integer learningBurnoutScoreGood= 0;
    /**
     * 道德性 指标计分
     */
    private Double moralScore= 0D;
    private Double moralScoreDiff= 0D;
    private Integer moralScoreVeryBad= 0;
    private Integer moralScoreBad= 0;
    private Integer moralScoreGenerally= 0;
    private Integer moralScoreGood= 0;
    /**
     * 稳定性 指标计分
     */
    private Double stabilityScore= 0D;
    private Double stabilityScoreDiff= 0D;
    private Integer stabilityScoreVeryBad= 0;
    private Integer stabilityScoreBad= 0;
    private Integer stabilityScoreGenerally= 0;
    private Integer stabilityScoreGood= 0;
    /**
     * 纪律性 指标计分
     */
    private Double disciplineScore= 0D;
    private Double disciplineScoreDiff= 0D;
    private Integer disciplineScoreVeryBad= 0;
    private Integer disciplineScoreBad= 0;
    private Integer disciplineScoreGenerally= 0;
    private Integer disciplineScoreGood= 0;
    /**
     * 其他表现 指标计分
     */
    private Double otherPerformanceScore= 0D;
    private Double otherPerformanceScoreDiff= 0D;
    private Integer otherPerformanceScoreVeryBad= 0;
    private Integer otherPerformanceScoreBad= 0;
    private Integer otherPerformanceScoreGenerally= 0;
    private Integer otherPerformanceScoreGood= 0;
    /**
     * 情绪管理 指标计分
     */
    private Double emotionManagementScore= 0D;
    private Double emotionManagementScoreDiff= 0D;
    private Integer emotionManagementScoreVeryBad= 0;
    private Integer emotionManagementScoreBad= 0;
    private Integer emotionManagementScoreGenerally= 0;
    private Integer emotionManagementScoreGood= 0;
    /**
     * 目标激励 指标计分
     */
    private Double goalMotivationScore= 0D;
    private Double goalMotivationScoreDiff= 0D;
    private Integer goalMotivationScoreVeryBad= 0;
    private Integer goalMotivationScoreBad= 0;
    private Integer goalMotivationScoreGenerally= 0;
    private Integer goalMotivationScoreGood= 0;
    /**
     * 积极关注 指标计分
     */
    private Double positiveAttentionScore= 0D;
    private Double positiveAttentionScoreDiff= 0D;
    private Integer positiveAttentionScoreVeryBad= 0;
    private Integer positiveAttentionScoreBad= 0;
    private Integer positiveAttentionScoreGenerally= 0;
    private Integer positiveAttentionScoreGood= 0;
    /**
     * 学校支持 指标计分
     */
    private Double schoolSupportScore= 0D;
    private Double schoolSupportScoreDiff= 0D;
    private Integer schoolSupportScoreVeryBad= 0;
    private Integer schoolSupportScoreBad= 0;
    private Integer schoolSupportScoreGenerally= 0;
    private Integer schoolSupportScoreGood= 0;
    /**
     * 人际支持 指标计分
     */
    private Double interpersonalSupportScore= 0D;
    private Double interpersonalSupportScoreDiff= 0D;
    private Integer interpersonalSupportScoreVeryBad= 0;
    private Integer interpersonalSupportScoreBad= 0;
    private Integer interpersonalSupportScoreGenerally= 0;
    private Integer interpersonalSupportScoreGood= 0;
    /**
     * 家庭支持 指标计分
     */
    private Double familySupportScore= 0D;
    private Double familySupportScoreDiff= 0D;
    private Integer familySupportScoreVeryBad= 0;
    private Integer familySupportScoreBad= 0;
    private Integer familySupportScoreGenerally= 0;
    private Integer familySupportScoreGood= 0;
    /**
     * 学习压力 指标计分
     */
    private Double studyStressScore= 0D;
    private Double studyStressScoreDiff= 0D;
    private Integer studyStressScoreVeryBad= 0;
    private Integer studyStressScoreBad= 0;
    private Integer studyStressScoreGenerally= 0;
    private Integer studyStressScoreGood= 0;
    /**
     * 人际压力 指标计分
     */
    private Double interpersonalStressScore= 0D;
    private Double interpersonalStressScoreDiff= 0D;
    private Integer interpersonalStressScoreVeryBad= 0;
    private Integer interpersonalStressScoreBad= 0;
    private Integer interpersonalStressScoreGenerally= 0;
    private Integer interpersonalStressScoreGood= 0;
    /**
     * 受惩罚压力 指标计分
     */
    private Double punishmentStressScore= 0D;
    private Double punishmentStressScoreDiff= 0D;
    private Integer punishmentStressScoreVeryBad= 0;
    private Integer punishmentStressScoreBad= 0;
    private Integer punishmentStressScoreGenerally= 0;
    private Integer punishmentStressScoreGood= 0;
    /**
     * 丧失压力 指标计分
     */
    private Double lossStressScore= 0D;
    private Double lossStressScoreDiff= 0D;
    private Integer lossStressScoreVeryBad= 0;
    private Integer lossStressScoreBad= 0;
    private Integer lossStressScoreGenerally= 0;
    private Integer lossStressScoreGood= 0;
    /**
     * 适应压力 指标计分
     */
    private Double adaptationStressScore= 0D;
    private Double adaptationStressScoreDiff= 0D;
    private Integer adaptationStressScoreVeryBad= 0;
    private Integer adaptationStressScoreBad= 0;
    private Integer adaptationStressScoreGenerally= 0;
    private Integer adaptationStressScoreGood= 0;
    /**
     * 强迫 指标计分
     */
    private Double compulsionScore= 0D;
    private Double compulsionScoreDiff= 0D;
    private Integer compulsionScoreVeryBad= 0;
    private Integer compulsionScoreBad= 0;
    private Integer compulsionScoreGenerally= 0;
    private Integer compulsionScoreGood= 0;
    /**
     * 人偏执 指标计分
     */
    private Double paranoiaScore= 0D;
    private Double paranoiaScoreDiff= 0D;
    private Integer paranoiaScoreVeryBad= 0;
    private Integer paranoiaScoreBad= 0;
    private Integer paranoiaScoreGenerally= 0;
    private Integer paranoiaScoreGood= 0;
    /**
     * 敌对 指标计分
     */
    private Double hostilityScore= 0D;
    private Double hostilityScoreDiff= 0D;
    private Integer hostilityScoreVeryBad= 0;
    private Integer hostilityScoreBad= 0;
    private Integer hostilityScoreGenerally= 0;
    private Integer hostilityScoreGood= 0;
    /**
     * 人际敏感 指标计分
     */
    private Double interpersonalSensitivityScore= 0D;
    private Double interpersonalSensitivityScoreDiff= 0D;
    private Integer interpersonalSensitivityScoreVeryBad= 0;
    private Integer interpersonalSensitivityScoreBad= 0;
    private Integer interpersonalSensitivityScoreGenerally= 0;
    private Integer interpersonalSensitivityScoreGood= 0;
    /**
     * 焦虑 指标计分
     */
    private Double anxietyScore= 0D;
    private Double anxietyScoreDiff= 0D;
    private Integer anxietyScoreVeryBad= 0;
    private Integer anxietyScoreBad= 0;
    private Integer anxietyScoreGenerally= 0;
    private Integer anxietyScoreGood= 0;
    /**
     * 抑郁 指标计分
     */
    private Double depressionScore= 0D;
    private Double depressionScoreDiff= 0D;
    private Integer depressionScoreVeryBad= 0;
    private Integer depressionScoreBad= 0;
    private Integer depressionScoreGenerally= 0;
    private Integer depressionScoreGood= 0;

    /**
     * 睡眠指数
     */
    private Double sleepDiff= 0D;
    private Integer sleepVeryBad= 0;
    private Integer sleepBad= 0;
    private Integer sleepGenerally= 0;
    private Integer sleepGood= 0;
}
