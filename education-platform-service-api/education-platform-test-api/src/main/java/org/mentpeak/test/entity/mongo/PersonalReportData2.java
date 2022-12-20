package org.mentpeak.test.entity.mongo;

import lombok.Data;


/**
 * 个人报告基础数据
 *
 * @author demain_lee
 * @since 2022-08-09
 */
@Data
public class PersonalReportData2 {

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 试卷ID
     */
    private String paperId;

    /**
     * 总人数
     */
    private Integer peopleCount;

    // 维度分

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
    /**
     * 作答有效性 计分
     */
    private Double validityAnswersScore= 0D;
    /**
     * 家长他评 维度计分
     */
    private Double parentalAssessmentScore= 0D;
    /**
     * 教师他评 维度计分
     */
    private Double teacherRatingsScore= 0D;

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
    private Double learningAttitudeScore= 0D;
    /**
     * 时间管理 指标计分
     */
    private Double timeManagementScore= 0D;
    /**
     * 学习倦怠 指标计分
     */
    private Double learningBurnoutScore= 0D;
    /**
     * 道德性 指标计分
     */
    private Double moralScore= 0D;
    /**
     * 稳定性 指标计分
     */
    private Double stabilityScore= 0D;
    /**
     * 纪律性 指标计分
     */
    private Double disciplineScore= 0D;
    /**
     * 其他表现 指标计分
     */
    private Double otherPerformanceScore= 0D;
    /**
     * 情绪管理 指标计分
     */
    private Double emotionManagementScore= 0D;
    /**
     * 目标激励 指标计分
     */
    private Double goalMotivationScore= 0D;
    /**
     * 积极关注 指标计分
     */
    private Double positiveAttentionScore= 0D;
    /**
     * 学校支持 指标计分
     */
    private Double schoolSupportScore= 0D;
    /**
     * 人际支持 指标计分
     */
    private Double interpersonalSupportScore= 0D;
    /**
     * 家庭支持 指标计分
     */
    private Double familySupportScore= 0D;
    /**
     * 学习压力 指标计分
     */
    private Double studyStressScore= 0D;
    /**
     * 人际压力 指标计分
     */
    private Double interpersonalStressScore= 0D;
    /**
     * 受惩罚压力 指标计分
     */
    private Double punishmentStressScore= 0D;
    /**
     * 丧失压力 指标计分
     */
    private Double lossStressScore= 0D;
    /**
     * 适应压力 指标计分
     */
    private Double adaptationStressScore= 0D;
    /**
     * 强迫 指标计分
     */
    private Double compulsionScore= 0D;
    /**
     * 人偏执 指标计分
     */
    private Double paranoiaScore= 0D;
    /**
     * 敌对 指标计分
     */
    private Double hostilityScore= 0D;
    /**
     * 人际敏感 指标计分
     */
    private Double interpersonalSensitivityScore= 0D;
    /**
     * 焦虑 指标计分
     */
    private Double anxietyScore= 0D;
    /**
     * 抑郁 指标计分
     */
    private Double depressionScore= 0D;

}