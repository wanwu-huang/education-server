package org.mentpeak.test.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 预警管理信息
 *
 * @author: demain_lee
 * @since: 2022-07-14
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class TaskWarnVO extends TaskInfoVO {

    @ApiModelProperty(value = "预警人数")
    private Integer warnPeopleCount;

}
