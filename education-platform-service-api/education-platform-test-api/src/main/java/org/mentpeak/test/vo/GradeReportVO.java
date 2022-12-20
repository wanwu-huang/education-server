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
public class GradeReportVO implements Serializable {

    @ApiModelProperty(value = "标题")
    private String title;

    @ApiModelProperty(value = "测试完成情况")
    private GTestFInishVO testModuleVO;

    @ApiModelProperty(value = "作答有效性")
    private GradeInvalidVO gradeInvalidVOList;

    @ApiModelProperty(value = "心理健康评级")
    private GTestModuleTwoVO testModuleTwoVO;

    @ApiModelProperty(value = "测评概况")
    private PersonalReport.TestOverview testOverview;

    @ApiModelProperty(value = "维度模块")
    private List<GDimensionReportVO> dimensionList;
}
