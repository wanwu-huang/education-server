package org.mentpeak.test.vo;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.mentpeak.test.entity.ReportTeacherUser;

/**
 * 老师用户报告表视图实体类
 *
 * @author lxp
 * @since 2022-07-12
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "ReportTeacherUserVO对象", description = "老师用户报告表")
public class ReportTeacherUserVO extends ReportTeacherUser {
	private static final long serialVersionUID = 1L;

}
