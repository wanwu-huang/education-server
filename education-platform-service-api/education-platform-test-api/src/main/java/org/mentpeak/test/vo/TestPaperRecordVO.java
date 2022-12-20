package org.mentpeak.test.vo;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.mentpeak.test.entity.TestPaperRecord;

/**
 * 用户问卷测试记录表视图实体类
 *
 * @author lxp
 * @since 2022-07-13
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "TestPaperRecordVO对象", description = "用户问卷测试记录表")
public class TestPaperRecordVO extends TestPaperRecord {
    private static final long serialVersionUID = 1L;

}
