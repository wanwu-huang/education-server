package org.mentpeak.test.wrapper;

import lombok.AllArgsConstructor;
import org.mentpeak.core.mybatisplus.support.BaseEntityWrapper;
import org.mentpeak.core.tool.utils.BeanUtil;
import org.mentpeak.test.entity.TestTeacherPaperRecord;
import org.mentpeak.test.vo.TestTeacherPaperRecordVO;

/**
 * 教师评定问卷测试记录表包装类,返回视图层所需的字段
 *
 * @author lxp
 * @since 2022-08-15
 */
@AllArgsConstructor
public class TestTeacherPaperRecordWrapper extends BaseEntityWrapper<TestTeacherPaperRecord, TestTeacherPaperRecordVO>  {


	@Override
	public TestTeacherPaperRecordVO entityVO(TestTeacherPaperRecord testTeacherPaperRecord) {
		TestTeacherPaperRecordVO testTeacherPaperRecordVO = BeanUtil.copy(testTeacherPaperRecord, TestTeacherPaperRecordVO.class);


		return testTeacherPaperRecordVO;
	}

}
