package org.mentpeak.test.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.mentpeak.core.auth.utils.SecureUtil;
import org.mentpeak.core.mybatisplus.base.BaseServiceImpl;
import org.mentpeak.test.entity.TestPaperRecord;
import org.mentpeak.test.mapper.TestPaperRecordMapper;
import org.mentpeak.test.service.ITestPaperRecordService;
import org.mentpeak.test.vo.TestPaperRecordVO;
import org.mentpeak.test.vo.TestRecordVO;
import org.springframework.stereotype.Service;

/**
 * 用户问卷测试记录表 服务实现类
 *
 * @author lxp
 * @since 2022-07-13
 */
@Service
public class TestPaperRecordServiceImpl extends BaseServiceImpl<TestPaperRecordMapper, TestPaperRecord> implements ITestPaperRecordService {

    @Override
    public IPage<TestPaperRecordVO> selectTestPaperRecordPage(IPage<TestPaperRecordVO> page, TestPaperRecordVO testPaperRecord) {
        return page.setRecords(baseMapper.selectTestPaperRecordPage(page, testPaperRecord));
    }

    @Override
    public IPage<TestRecordVO> getList(IPage<TestRecordVO> page) {
        Long userId = SecureUtil.getUserId();
        return baseMapper.getList(page, userId);
    }

}
