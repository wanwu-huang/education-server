package org.mentpeak.test.vo;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.mentpeak.test.entity.ReportFamilyStructure;

/**
 * 家庭结构视图实体类
 *
 * @author lxp
 * @since 2022-07-12
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "ReportFamilyStructureVO对象", description = "家庭结构")
public class ReportFamilyStructureVO extends ReportFamilyStructure {
	private static final long serialVersionUID = 1L;

}
