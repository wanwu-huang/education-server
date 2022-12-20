package org.mentpeak.test.vo;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.mentpeak.test.entity.TestTeacherPaperRecord;

/**
 * 教师评定问卷测试记录表视图实体类
 *
 * @author lxp
 * @since 2022-08-15
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "TestTeacherPaperRecordVO对象", description = "教师评定问卷测试记录表")
public class TestTeacherPaperRecordVO extends TestTeacherPaperRecord {
	private static final long serialVersionUID = 1L;

}
