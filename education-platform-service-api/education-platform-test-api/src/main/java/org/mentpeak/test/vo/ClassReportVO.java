package org.mentpeak.test.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.mentpeak.test.entity.mongo.PersonalReport;

import java.io.Serializable;
import java.util.List;

/**
 * @author hzl
 * @data 2022年08月16日16:48
 */
@Data
public class ClassReportVO implements Serializable {

    @ApiModelProperty(value = "是否有权限 0：没有 1：有")
    private Integer isPermission = 1;

    @ApiModelProperty(value = "标题")
    private String title;

    @ApiModelProperty(value = "测试完成情况")
    private TestModuleVO testModuleVO;

    @ApiModelProperty(value = "作答有效性")
    private InvalidVO invalidVO;

    @ApiModelProperty(value = "心理健康评级")
    private TestModuleTwoVO testModuleTwoVO;

    @ApiModelProperty(value = "测评概况")
    private PersonalReport.TestOverview testOverview;

    @ApiModelProperty(value = "维度模块")
    private List<DimensionReportVO> dimensionList;
}
