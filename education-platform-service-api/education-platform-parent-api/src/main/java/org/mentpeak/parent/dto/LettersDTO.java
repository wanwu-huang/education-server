package org.mentpeak.parent.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.mentpeak.parent.entity.Letters;

/**
 * 家长端-匿名信表数据传输对象实体类
 *
 * @author lxp
 * @since 2022-06-15
 */
@Data
public class LettersDTO {
    /**
     * 信件信息
     */
    @ApiModelProperty(value = "信件信息", required = true)
    private String lettersContent;
    /**
     * 信件信息
     */
    @ApiModelProperty(value = "学校名称")
    private String schoolName;

}
