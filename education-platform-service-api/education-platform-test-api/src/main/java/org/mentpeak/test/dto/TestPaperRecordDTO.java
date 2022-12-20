package org.mentpeak.test.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.mentpeak.test.entity.TestPaperRecord;

/**
 * 用户问卷测试记录表数据传输对象实体类
 *
 * @author lxp
 * @since 2022-07-13
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class TestPaperRecordDTO extends TestPaperRecord {
    private static final long serialVersionUID = 1L;

}
