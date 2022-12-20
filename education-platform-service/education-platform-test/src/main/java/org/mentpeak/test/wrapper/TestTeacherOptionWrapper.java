package org.mentpeak.test.wrapper;

import lombok.AllArgsConstructor;
import org.mentpeak.core.mybatisplus.support.BaseEntityWrapper;
import org.mentpeak.core.tool.utils.BeanUtil;
import org.mentpeak.test.entity.TestTeacherOption;
import org.mentpeak.test.vo.TestTeacherOptionVO;

/**
 * 教师评定题支信息表包装类,返回视图层所需的字段
 *
 * @author lxp
 * @since 2022-08-15
 */
@AllArgsConstructor
public class TestTeacherOptionWrapper extends BaseEntityWrapper<TestTeacherOption, TestTeacherOptionVO>  {


	@Override
	public TestTeacherOptionVO entityVO(TestTeacherOption testTeacherOption) {
		TestTeacherOptionVO testTeacherOptionVO = BeanUtil.copy(testTeacherOption, TestTeacherOptionVO.class);


		return testTeacherOptionVO;
	}

}
