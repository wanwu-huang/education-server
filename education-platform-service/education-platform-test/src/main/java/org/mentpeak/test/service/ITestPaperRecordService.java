package org.mentpeak.test.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.mentpeak.core.mybatisplus.base.BaseService;
import org.mentpeak.test.entity.TestPaperRecord;
import org.mentpeak.test.vo.TestPaperRecordVO;
import org.mentpeak.test.vo.TestRecordVO;

/**
 * 用户问卷测试记录表 服务类
 *
 * @author lxp
 * @since 2022-07-13
 */
public interface ITestPaperRecordService extends BaseService<TestPaperRecord> {

    /**
     * 自定义分页
     *
     * @param page
     * @param testPaperRecord
     * @return
     */
    IPage<TestPaperRecordVO> selectTestPaperRecordPage(IPage<TestPaperRecordVO> page, TestPaperRecordVO testPaperRecord);

    IPage<TestRecordVO> getList(IPage<TestRecordVO> page);

}
