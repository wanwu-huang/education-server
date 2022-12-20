package org.mentpeak.test.vo;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.mentpeak.test.entity.ReportLifeStyle;

/**
 * 生活方式（是否与父母生活）视图实体类
 *
 * @author lxp
 * @since 2022-07-12
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "ReportLifeStyleVO对象", description = "生活方式（是否与父母生活）")
public class ReportLifeStyleVO extends ReportLifeStyle {
	private static final long serialVersionUID = 1L;

}
