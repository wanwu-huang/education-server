package org.mentpeak.test.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author hzl
 * @data 2022年08月16日19:09
 */
@Data
public class TargetResultVO implements Serializable {

    @ApiModelProperty(value = "指标结果")
    private List<TargetVO> targetVOList = new ArrayList<>();

    @ApiModelProperty(value = "名字")
    private List<TitleNameVO> titleNameVOList = new ArrayList<>();

    @Data
    public static class TargetVO {
        @ApiModelProperty(value = "标题")
        private String title;

        @ApiModelProperty(value = "分数")
        private String scoreOne;

        @ApiModelProperty(value = "字体颜色")
        private String fontColorOne;

        @ApiModelProperty(value = "分数2")
        private String scoreTwo;

        @ApiModelProperty(value = "字体颜色2")
        private String fontColorTwo;
    }

    @Data
    public static class TitleNameVO {
        @ApiModelProperty(value = "名称")
        private String name;

        @ApiModelProperty(value = "颜色")
        private String color;
    }
}
