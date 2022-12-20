package org.mentpeak.parent.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.mentpeak.test.LogicOption;

import java.io.Serializable;
import java.util.List;

/**
 * @author lxp
 * @date 2022/06/19 13:21
 **/
@Data
@ApiModel(value = "家长他评问卷", description = "家长他评问卷")
public class ParentPaperDTO implements Serializable {
	private static final long serialVersionUID = 861686071393251386L;

	@ApiModelProperty(value = "题目列表")
	List<ResponseQuestionDto<LogicOption>> responseQuestionDtos;

	@ApiModelProperty(value = "试卷ids")
	private Long paperId;

	@ApiModelProperty(value = "试卷ids")
	private String name;
}
