package org.mentpeak.test.entity.mongo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.mentpeak.test.vo.DimensionReportVO;
import org.mentpeak.test.vo.InvalidVO;
import org.mentpeak.test.vo.TestModuleTwoVO;
import org.mentpeak.test.vo.TestModuleVO;

import java.io.Serializable;
import java.util.List;

/**
 * @author hzl
 * @data 2022年08月16日16:48
 */
@Data
public class ClassReport implements Serializable {

    @ApiModelProperty(value = "ID")
    private String id;

    @ApiModelProperty(value = "年级ID")
    private Long gradeId;

    @ApiModelProperty(value = "班级ID")
    private Long classId;

    @ApiModelProperty(value = "任务ID")
    private Long taskId;

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
