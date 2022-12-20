package org.mentpeak.test.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author hzl
 * @data 2021年12月03日14:02
 */
@Data
public class TestFinishVo implements Serializable {

    @ApiModelProperty(value = "测评人群")
    private String evaluationCrowd;
    @ApiModelProperty(value = "参与班级")
    private String className;
    @ApiModelProperty(value = "实际人数")
    private Integer totalPeople;
    @ApiModelProperty(value = "已测人数")
    private Integer testPeople;
    @ApiModelProperty(value = "未测人数")
    private Integer noTestPeople;
    @ApiModelProperty(value = "无效人数")
    private Integer invalidPeople;
    @ApiModelProperty(value = "完成率")
    private String completionRate;

}
