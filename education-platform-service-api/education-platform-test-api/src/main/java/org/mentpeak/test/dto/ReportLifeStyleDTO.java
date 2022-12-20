package org.mentpeak.test.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.mentpeak.test.entity.ReportLifeStyle;

/**
 * 生活方式（是否与父母生活）数据传输对象实体类
 *
 * @author lxp
 * @since 2022-07-12
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ReportLifeStyleDTO extends ReportLifeStyle {
	private static final long serialVersionUID = 1L;

}
