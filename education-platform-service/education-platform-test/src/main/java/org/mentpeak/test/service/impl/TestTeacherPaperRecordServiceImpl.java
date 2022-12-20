package org.mentpeak.test.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.mentpeak.core.mybatisplus.base.BaseServiceImpl;
import org.mentpeak.test.entity.TestTeacherPaperRecord;
import org.mentpeak.test.mapper.TestTeacherPaperRecordMapper;
import org.mentpeak.test.service.ITestTeacherPaperRecordService;
import org.mentpeak.test.vo.TestTeacherPaperRecordVO;
import org.springframework.stereotype.Service;

/**
 * 教师评定问卷测试记录表 服务实现类
 *
 * @author lxp
 * @since 2022-08-15
 */
@Service
public class TestTeacherPaperRecordServiceImpl extends BaseServiceImpl<TestTeacherPaperRecordMapper, TestTeacherPaperRecord> implements ITestTeacherPaperRecordService {

	@Override
	public IPage<TestTeacherPaperRecordVO> selectTestTeacherPaperRecordPage(IPage<TestTeacherPaperRecordVO> page, TestTeacherPaperRecordVO testTeacherPaperRecord) {
		return page.setRecords(baseMapper.selectTestTeacherPaperRecordPage(page, testTeacherPaperRecord));
	}

}
