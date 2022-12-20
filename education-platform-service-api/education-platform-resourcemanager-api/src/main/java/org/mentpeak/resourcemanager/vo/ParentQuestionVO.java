package org.mentpeak.resourcemanager.vo;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.mentpeak.resourcemanager.entity.ParentQuestion;

/**
 * 家长他评问卷题目信息表视图实体类
 *
 * @author lxp
 * @since 2022-06-17
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "ParentQuestionVO对象", description = "家长他评问卷题目信息表")
public class ParentQuestionVO extends ParentQuestion {
	private static final long serialVersionUID = 1L;

}
