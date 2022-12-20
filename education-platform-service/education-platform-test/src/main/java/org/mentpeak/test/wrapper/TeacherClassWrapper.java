package org.mentpeak.test.wrapper;

import lombok.AllArgsConstructor;
import org.mentpeak.core.mybatisplus.support.BaseEntityWrapper;
import org.mentpeak.core.tool.utils.BeanUtil;
import org.mentpeak.test.entity.TeacherClass;
import org.mentpeak.test.vo.TeacherClassVO;

/**
 * 班主任班级关联表包装类,返回视图层所需的字段
 *
 * @author lxp
 * @since 2022-07-12
 */
@AllArgsConstructor
public class TeacherClassWrapper extends BaseEntityWrapper<TeacherClass, TeacherClassVO>  {


	@Override
	public TeacherClassVO entityVO(TeacherClass teacherClass) {
		TeacherClassVO teacherClassVO = BeanUtil.copy(teacherClass, TeacherClassVO.class);


		return teacherClassVO;
	}

}
