package org.mentpeak.test.wrapper;

import lombok.AllArgsConstructor;
import org.mentpeak.core.mybatisplus.support.BaseEntityWrapper;
import org.mentpeak.core.tool.utils.BeanUtil;
import org.mentpeak.test.entity.TestPaperRecord;
import org.mentpeak.test.vo.TestPaperRecordVO;

/**
 * 用户问卷测试记录表包装类,返回视图层所需的字段
 *
 * @author lxp
 * @since 2022-07-13
 */
@AllArgsConstructor
public class TestPaperRecordWrapper extends BaseEntityWrapper<TestPaperRecord, TestPaperRecordVO> {


    @Override
    public TestPaperRecordVO entityVO(TestPaperRecord testPaperRecord) {
        TestPaperRecordVO testPaperRecordVO = BeanUtil.copy(testPaperRecord, TestPaperRecordVO.class);


        return testPaperRecordVO;
    }

}
