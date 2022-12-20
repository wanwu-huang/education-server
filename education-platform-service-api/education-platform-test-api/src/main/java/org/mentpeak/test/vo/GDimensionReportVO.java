package org.mentpeak.test.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.poi.ss.formula.functions.T;

import java.io.Serializable;
import java.util.*;

/**
 * @author hzl
 * @data 2022年08月16日17:30
 * 年级维度报告
 */
@Data
public class GDimensionReportVO implements Serializable {

    @ApiModelProperty(value = "总体结果")
    private List<TotalResultVO> totalResultVOList = new ArrayList<>();
    @ApiModelProperty(value = "各指标结果")
    private TargetResultVO targetResultVO = new TargetResultVO();
    @ApiModelProperty(value = "典型学生分布")
    private List<TypicalStudent> typicalStudentList = new ArrayList<>();
    @ApiModelProperty(value = "班级差异情况")
    private List<Object> classDiffList = new ArrayList<>();
    @ApiModelProperty(value = "指标结果说明")
    private List<String> illustrate = new ArrayList<>();
    @ApiModelProperty(value = "睡眠指数-典型学生名单")
    private List<FollowUserVO> followUser = new ArrayList<>();


    @Data
    public static class ClassDiffOne {
        @ApiModelProperty(value = "参与班级")
        private String title;
        @ApiModelProperty(value = "指标一")
        private TargetScore targetOne;
        @ApiModelProperty(value = "指标二")
        private TargetScore targetTwo;
        @ApiModelProperty(value = "指标三")
        private TargetScore targetThree;
    }

    @Data
    public static class ClassDiffTwo {
        @ApiModelProperty(value = "参与班级")
        private String title;
        @ApiModelProperty(value = "指标一")
        private TargetScore targetOne;
        @ApiModelProperty(value = "指标二")
        private TargetScore targetTwo;
        @ApiModelProperty(value = "指标三")
        private TargetScore targetThree;
        @ApiModelProperty(value = "指标四")
        private TargetScore targetFour;
    }

    @Data
    public static class ClassDiffThree {
        @ApiModelProperty(value = "参与班级")
        private String title;
        @ApiModelProperty(value = "指标一")
        private TargetScore targetOne;
        @ApiModelProperty(value = "指标二")
        private TargetScore targetTwo;
        @ApiModelProperty(value = "指标三")
        private TargetScore targetThree;
        @ApiModelProperty(value = "指标四")
        private TargetScore targetFour;
        @ApiModelProperty(value = "指标五")
        private TargetScore targetFive;
    }
    @Data
    public static class ClassDiffFour {
        @ApiModelProperty(value = "参与班级")
        private String title;
        @ApiModelProperty(value = "指标一")
        private TargetScore targetOne;
        @ApiModelProperty(value = "指标二")
        private TargetScore targetTwo;
        @ApiModelProperty(value = "指标三")
        private TargetScore targetThree;
        @ApiModelProperty(value = "指标四")
        private TargetScore targetFour;
        @ApiModelProperty(value = "指标五")
        private TargetScore targetFive;
        @ApiModelProperty(value = "指标六")
        private TargetScore targetSix;
    }

    @Data
    public static class ClassDiffFive {
        @ApiModelProperty(value = "参与班级")
        private String title;
        @ApiModelProperty(value = "指标一")
        private TargetScore targetOne;
    }


    @Data
    public static class TargetScore {
        @ApiModelProperty(value = "指标分数")
        private String targetScore;
        @ApiModelProperty(value = "指标差异性1较差 2没有差异 3较好")
        private int targetDiff;
    }

    @Data
    public static class TypicalStudent {
        @ApiModelProperty(value = "名称")
        private String title;
        @ApiModelProperty(value = "很差")
        private String veryBad;
        @ApiModelProperty(value = "较差")
        private String bad;
        @ApiModelProperty(value = "一般")
        private String generally;
        @ApiModelProperty(value = "良好")
        private String good;
    }
}
