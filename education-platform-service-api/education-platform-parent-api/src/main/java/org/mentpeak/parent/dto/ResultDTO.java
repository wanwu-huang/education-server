package org.mentpeak.parent.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.mentpeak.parent.entity.QResult;
import org.springframework.validation.annotation.Validated;

import java.io.Serializable;
import java.util.List;

/**
 * <p>
 * 试卷和题干及选中题支关系表
 * </p>
 *
 * @author 明天
 * @since 2021-10-29
 */
@Data
public class ResultDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "答案数组")
    private List<QResult> result;

    @ApiModelProperty(value = "试卷id")
    private Long paperId;


}
