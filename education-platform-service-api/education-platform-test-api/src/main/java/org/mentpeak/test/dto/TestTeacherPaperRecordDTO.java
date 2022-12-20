package org.mentpeak.test.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.mentpeak.test.entity.TestTeacherPaperRecord;

/**
 * 教师评定问卷测试记录表数据传输对象实体类
 *
 * @author lxp
 * @since 2022-08-15
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class TestTeacherPaperRecordDTO extends TestTeacherPaperRecord {
	private static final long serialVersionUID = 1L;

}
