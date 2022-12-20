package org.mentpeak.test.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.mentpeak.test.entity.ReportLifeStyleTaskUser;

/**
 * 生活方式任务用户关联表数据传输对象实体类
 *
 * @author lxp
 * @since 2022-07-12
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ReportLifeStyleTaskUserDTO extends ReportLifeStyleTaskUser {
	private static final long serialVersionUID = 1L;

}
