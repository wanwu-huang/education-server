package org.mentpeak.test.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author hzl
 * @data 2022年08月16日17:30
 */
@Data
public class DimensionReportVO implements Serializable {

    @ApiModelProperty(value = "总体结果")
    private List<TotalResultVO> totalResultVOList = new ArrayList<>();
    @ApiModelProperty(value = "各指标结果")
    private TargetResultVO targetResultVO = new TargetResultVO();
    @ApiModelProperty(value = "指标结果说明")
    private TargetResult targetResult = new TargetResult();
    @ApiModelProperty(value = "差异性")
    private DiffCheck diffCheck = new DiffCheck();
    @ApiModelProperty(value = "差异表格")
    private List<Object> classDiffList = new ArrayList<>();
    @ApiModelProperty(value = "典型学生分布")
    private List<TypicalStudent> typicalStudentList = new ArrayList<>();
    @ApiModelProperty(value = "典型学生名单")
    private List<TypicalStudentName> typicalNameList = new ArrayList<>();

    @Data
    public static class TargetResult {
        @ApiModelProperty(value = "情况较好指标")
        private String[] goodTarget;
        @ApiModelProperty(value = "没有差异指标")
        private String[] noTarget;
        @ApiModelProperty(value = "情况较差指标")
        private String[] badTarget;
    }

    @Data
    public static class DiffCheck {
        @ApiModelProperty(value = "差异检验")
        private String diffent;
        @ApiModelProperty(value = "说明")
        private String explanation;
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

    @Data
    public static class TypicalStudentName {
        @ApiModelProperty(value = "名称")
        private String title;
        @ApiModelProperty(value = "很差")
        private List<UserInfoVO> veryBadList;
        @ApiModelProperty(value = "较差")
        private List<UserInfoVO> badList;
        @ApiModelProperty(value = "一般")
        private List<UserInfoVO> generallyList;
    }
}
