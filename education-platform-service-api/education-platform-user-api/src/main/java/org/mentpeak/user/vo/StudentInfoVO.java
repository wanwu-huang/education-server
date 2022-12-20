package org.mentpeak.user.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author hzl
 * @data 2022年07月12日19:09
 */
@Data
public class StudentInfoVO implements Serializable {
    private static final long serialVersionUID = 1L;
    @ApiModelProperty("id")
    private Long id;
    @ApiModelProperty("账号")
    private String account;
    @ApiModelProperty("姓名")
    private String name;
    @ApiModelProperty("年级")
    private String grade;
    @ApiModelProperty("班级")
    private String className;
}
