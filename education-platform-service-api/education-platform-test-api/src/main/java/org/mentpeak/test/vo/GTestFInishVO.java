package org.mentpeak.test.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author hzl
 * @data 2022年08月11日15:09
 * 年级-测评完成情况
 */
@Data
public class GTestFInishVO implements Serializable {

    @ApiModelProperty(value = "已测人数")
    private Integer totalTestPeople;
    @ApiModelProperty(value = "未测人数")
    private Integer totalNoTestPeople;
    @ApiModelProperty(value = "无效人数")
    private Integer totalInvalidPeople;
    @ApiModelProperty(value = "完成率")
    private String totalCompletionRate;
    @ApiModelProperty(value = "参与班级-测试完成情况")
    private List<TestFinishVo> testFinishVoList = new ArrayList<>();
}
