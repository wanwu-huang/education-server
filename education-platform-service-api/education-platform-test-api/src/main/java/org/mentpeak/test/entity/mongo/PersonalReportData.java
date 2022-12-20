package org.mentpeak.test.entity.mongo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;


/**
 * 个人报告基础数据
 * @author demain_lee
 * @since 2022-08-09
 */
@Data
@Builder
public class PersonalReportData {

    /**
     * 主键
     */
    private String id;

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 试卷ID
     */
    private String paperId;

    // 维度分

    /**
     * 学习状态 维度计分
     */
    private Double studyStatusScore;
    /**
     * 品行表现 维度计分
     */
    private Double behaviorScore;
    /**
     * 心理韧性 维度计分
     */
    private Double mentalToughnessScore;
    /**
     * 综合压力 维度计分
     */
    private Double overallStressScore;
    /**
     * 情绪指数 维度计分
     */
    private Double emotionalIndexScore;
    /**
     * 睡眠指数 维度计分
     */
    private Double sleepIndexScore;
    /**
     * 自杀意念 维度计分
     */
    private Double suicidalIdeationScore;
    /**
     * 作答有效性 计分
     */
    private Double validityAnswersScore;
    /**
     * 家长他评 维度计分
     */
    private Double parentalAssessmentScore;
    /**
     * 教师他评 维度计分
     */
    private Double teacherRatingsScore;

    // 维度等级

    /**
     * 自杀意念（心理危机预警） 等级    0  未发现    1    1级预警      2     2级预警      3      3级预警
     */
    private Integer suicidalIdeationLevel;
    /**
     * 家长他评 等级   0  良好    1     1级
     */
    private Integer parentalAssessmentLevel;
    /**
     * 教师他评 等级   0  良好    1   1级    2    2级    3     3级
     */
    private Integer teacherRatingsLevel;

    // 指标分

    /**
     * 学习态度 指标计分
     */
    private Double learningAttitudeScore;
    /**
     * 时间管理 指标计分
     */
    private Double timeManagementScore;
    /**
     * 学习倦怠 指标计分
     */
    private Double learningBurnoutScore;
    /**
     * 道德性 指标计分
     */
    private Double moralScore;
    /**
     * 稳定性 指标计分
     */
    private Double stabilityScore;
    /**
     * 纪律性 指标计分
     */
    private Double disciplineScore;
    /**
     * 其他表现 指标计分
     */
    private Double otherPerformanceScore;
    /**
     * 情绪管理 指标计分
     */
    private Double emotionManagementScore;
    /**
     * 目标激励 指标计分
     */
    private Double goalMotivationScore;
    /**
     * 积极关注 指标计分
     */
    private Double positiveAttentionScore;
    /**
     * 学校支持 指标计分
     */
    private Double schoolSupportScore;
    /**
     * 人际支持 指标计分
     */
    private Double interpersonalSupportScore;
    /**
     * 家庭支持 指标计分
     */
    private Double familySupportScore;
    /**
     * 学习压力 指标计分
     */
    private Double studyStressScore;
    /**
     * 人际压力 指标计分
     */
    private Double interpersonalStressScore;
    /**
     * 受惩罚压力 指标计分
     */
    private Double punishmentStressScore;
    /**
     * 丧失压力 指标计分
     */
    private Double lossStressScore;
    /**
     * 适应压力 指标计分
     */
    private Double adaptationStressScore;
    /**
     * 强迫 指标计分
     */
    private Double compulsionScore;
    /**
     * 人偏执 指标计分
     */
    private Double paranoiaScore;
    /**
     * 敌对 指标计分
     */
    private Double hostilityScore;
    /**
     * 人际敏感 指标计分
     */
    private Double interpersonalSensitivityScore;
    /**
     * 焦虑 指标计分
     */
    private Double anxietyScore;
    /**
     * 抑郁 指标计分
     */
    private Double depressionScore;

    @ApiModelProperty(value = "创建时间")
    private String createTime;

}

