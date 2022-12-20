package org.mentpeak.parent.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author hzl
 * @data 2022年06月24日9:49
 * 填空题
 */
@Data
public class ExtResult {

    @ApiModelProperty(value = "输入内容")
    private String inputContent;

    private String imageUrl;

    private List<Files> files;
}
