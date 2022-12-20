package org.mentpeak.parent.vo;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.mentpeak.parent.entity.Letters;

/**
 * 家长端-匿名信表视图实体类
 *
 * @author lxp
 * @since 2022-06-15
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "LettersVO对象", description = "家长端-匿名信表")
public class LettersVO extends Letters {
	private static final long serialVersionUID = 1L;

}
