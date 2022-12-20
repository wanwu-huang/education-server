package org.mentpeak.test.entity.mongo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * 教师评定数据
 *
 * @author demain_lee
 * @since 2022-08-16
 */
@Data
@Builder
public class TeacherRating {


    private String id;

    @ApiModelProperty(value = "试卷ID")
    private Long paperId;

    @ApiModelProperty(value = "数据")
    private List<StuQuestionData> stuQuestionDataList;

    @Data
    public static class StuQuestionData {

        @ApiModelProperty(value = "编号")
        private Long userId;

        @ApiModelProperty(value = "姓名")
        private String realName;

        @ApiModelProperty(value = "学习成绩显著下滑")
        private QuestionData studentAchievement;

        @ApiModelProperty(value = "明显的情绪问题")
        private QuestionData emotionalProblems;

        @ApiModelProperty(value = "严重的人际冲突")
        private QuestionData interpersonalConflict;

        @ApiModelProperty(value = "欺负霸凌同学")
        private QuestionData bullyingClassmates;

        @ApiModelProperty(value = "被霸凌或被孤立")
        private QuestionData beIsolated;

        @ApiModelProperty(value = "严重的违纪行为")
        private QuestionData disciplinaryOffence;

    }


    @Data
    @Builder
    public static class QuestionData {

        @ApiModelProperty(value = "题干ID")
        private String questionId;

        @ApiModelProperty(value = "是否选中")
        private Integer select;

        @ApiModelProperty(value = "选项ID")
        private String[] optionId;

        @ApiModelProperty(value = "分数")
        private Integer[] score;
    }

}
