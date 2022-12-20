package org.mentpeak.test.vo;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.mentpeak.test.entity.ReortFamilyStructureTaskUser;

/**
 * 家庭结构任务用户关联表视图实体类
 *
 * @author lxp
 * @since 2022-07-12
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "ReortFamilyStructureTaskUserVO对象", description = "家庭结构任务用户关联表")
public class ReortFamilyStructureTaskUserVO extends ReortFamilyStructureTaskUser {
	private static final long serialVersionUID = 1L;

}
