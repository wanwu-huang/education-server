package org.mentpeak.test.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.mentpeak.test.base.BaseOption;

/**
 * 题支
 * @author demain_lee
 * @since 2022-08-17
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class TeacherOption extends BaseOption {
	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "分数")
	private Integer score;
}
