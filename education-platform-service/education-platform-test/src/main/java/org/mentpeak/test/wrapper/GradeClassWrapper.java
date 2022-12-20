package org.mentpeak.test.wrapper;

import lombok.AllArgsConstructor;
import org.mentpeak.core.mybatisplus.support.BaseEntityWrapper;
import org.mentpeak.core.tool.utils.BeanUtil;
import org.mentpeak.test.entity.GradeClass;
import org.mentpeak.test.vo.GradeClassVO;

/**
 * 年级-班级对应关联表包装类,返回视图层所需的字段
 *
 * @author lxp
 * @since 2022-08-12
 */
@AllArgsConstructor
public class GradeClassWrapper extends BaseEntityWrapper<GradeClass, GradeClassVO>  {


	@Override
	public GradeClassVO entityVO(GradeClass gradeClass) {
		GradeClassVO gradeClassVO = BeanUtil.copy(gradeClass, GradeClassVO.class);


		return gradeClassVO;
	}

}
