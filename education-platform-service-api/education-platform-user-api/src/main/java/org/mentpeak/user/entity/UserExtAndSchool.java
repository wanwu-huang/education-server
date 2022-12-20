package org.mentpeak.user.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author hzl
 * @data 2022年10月26日14:36
 */
@Data
public class UserExtAndSchool {
    /**
     * 学校/单位地址
     */
    @ApiModelProperty(value = "学校")
    private String address;

    /**
     * 年级
     */
    @ApiModelProperty(value = "年级")
    private String grade;

    /**
     * 班级ID
     */
    @ApiModelProperty(value = "班级ID")
    private Long classId;

}
