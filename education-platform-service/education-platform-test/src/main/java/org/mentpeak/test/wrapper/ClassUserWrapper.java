package org.mentpeak.test.wrapper;

import lombok.AllArgsConstructor;
import org.mentpeak.core.mybatisplus.support.BaseEntityWrapper;
import org.mentpeak.core.tool.utils.BeanUtil;
import org.mentpeak.test.entity.ClassUser;
import org.mentpeak.test.vo.ClassUserVO;

/**
 * 班级用户表包装类,返回视图层所需的字段
 *
 * @author lxp
 * @since 2022-07-12
 */
@AllArgsConstructor
public class ClassUserWrapper extends BaseEntityWrapper<ClassUser, ClassUserVO>  {


	@Override
	public ClassUserVO entityVO(ClassUser classUser) {
		ClassUserVO classUserVO = BeanUtil.copy(classUser, ClassUserVO.class);


		return classUserVO;
	}

}
