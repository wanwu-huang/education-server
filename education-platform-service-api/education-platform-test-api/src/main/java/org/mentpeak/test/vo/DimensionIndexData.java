package org.mentpeak.test.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;
import org.mentpeak.test.entity.mongo.PersonalReport;

import java.util.Map;

/**
 * 维度指标数据
 * @author demain_lee
 * @since 2022-08-10
 */
@Data
@Builder
public class DimensionIndexData {

    @ApiModelProperty(value = "维度总体数据")
    private PersonalReport.TotalResult totalData;

    @ApiModelProperty(value = "指标数据")
    private Map<Long, PersonalReport.ChartData> indexData;
}
