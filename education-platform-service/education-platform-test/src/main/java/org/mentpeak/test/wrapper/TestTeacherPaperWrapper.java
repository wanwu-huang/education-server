package org.mentpeak.test.wrapper;

import lombok.AllArgsConstructor;
import org.mentpeak.core.mybatisplus.support.BaseEntityWrapper;
import org.mentpeak.core.tool.utils.BeanUtil;
import org.mentpeak.test.entity.TestTeacherPaper;
import org.mentpeak.test.vo.TestTeacherPaperVO;

/**
 * 教师评定试卷信息表包装类,返回视图层所需的字段
 *
 * @author lxp
 * @since 2022-08-15
 */
@AllArgsConstructor
public class TestTeacherPaperWrapper extends BaseEntityWrapper<TestTeacherPaper, TestTeacherPaperVO>  {


	@Override
	public TestTeacherPaperVO entityVO(TestTeacherPaper testTeacherPaper) {
		TestTeacherPaperVO testTeacherPaperVO = BeanUtil.copy(testTeacherPaper, TestTeacherPaperVO.class);


		return testTeacherPaperVO;
	}

}
