package org.mentpeak.test.wrapper;

import lombok.AllArgsConstructor;
import org.mentpeak.core.mybatisplus.support.BaseEntityWrapper;
import org.mentpeak.core.tool.utils.BeanUtil;
import org.mentpeak.test.entity.TestTeacherConclusion;
import org.mentpeak.test.vo.TestTeacherConclusionVO;

/**
 * 教师他评结论包装类,返回视图层所需的字段
 *
 * @author lxp
 * @since 2022-07-12
 */
@AllArgsConstructor
public class TestTeacherConclusionWrapper extends BaseEntityWrapper<TestTeacherConclusion, TestTeacherConclusionVO>  {


	@Override
	public TestTeacherConclusionVO entityVO(TestTeacherConclusion testTeacherConclusion) {
		TestTeacherConclusionVO testTeacherConclusionVO = BeanUtil.copy(testTeacherConclusion, TestTeacherConclusionVO.class);


		return testTeacherConclusionVO;
	}

}
