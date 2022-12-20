package org.mentpeak.resourcemanager.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.mentpeak.resourcemanager.entity.ParentQuestion;

/**
 * 家长他评问卷题目信息表数据传输对象实体类
 *
 * @author lxp
 * @since 2022-06-17
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ParentQuestionDTO extends ParentQuestion {
	private static final long serialVersionUID = 1L;

}
