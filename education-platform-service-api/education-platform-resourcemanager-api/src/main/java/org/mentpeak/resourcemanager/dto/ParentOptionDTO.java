package org.mentpeak.resourcemanager.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.mentpeak.resourcemanager.entity.ParentOption;

/**
 * 家长他评问卷题支信息表数据传输对象实体类
 *
 * @author lxp
 * @since 2022-06-17
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ParentOptionDTO extends ParentOption {
	private static final long serialVersionUID = 1L;

}
