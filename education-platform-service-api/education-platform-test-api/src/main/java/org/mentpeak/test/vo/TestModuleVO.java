package org.mentpeak.test.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author hzl
 * @data 2022年08月11日15:09
 */
@Data
public class TestModuleVO implements Serializable {

    @ApiModelProperty(value = "测试完成情况")
    private List<TestFinishVo> testFinishVoList;
}
