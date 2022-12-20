package org.mentpeak.test.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.mentpeak.core.mybatisplus.base.BaseService;
import org.mentpeak.test.entity.TestTeacherPaperRecord;
import org.mentpeak.test.vo.TestTeacherPaperRecordVO;

/**
 * 教师评定问卷测试记录表 服务类
 *
 * @author lxp
 * @since 2022-08-15
 */
public interface ITestTeacherPaperRecordService extends BaseService<TestTeacherPaperRecord> {

	/**
	 * 自定义分页
	 *
	 * @param page
	 * @param testTeacherPaperRecord
	 * @return
	 */
	IPage<TestTeacherPaperRecordVO> selectTestTeacherPaperRecordPage(IPage<TestTeacherPaperRecordVO> page, TestTeacherPaperRecordVO testTeacherPaperRecord);

}
