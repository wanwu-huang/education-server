package org.mentpeak.parent.wrapper;

import lombok.AllArgsConstructor;
import org.mentpeak.core.mybatisplus.support.BaseEntityWrapper;
import org.mentpeak.core.tool.utils.BeanUtil;
import org.mentpeak.parent.entity.ParentOption;
import org.mentpeak.parent.vo.ParentOptionVO;

/**
 * 家长他评问卷题支信息表包装类,返回视图层所需的字段
 *
 * @author lxp
 * @since 2022-06-17
 */
@AllArgsConstructor
public class ParentOptionWrapper extends BaseEntityWrapper<ParentOption, ParentOptionVO>  {


	@Override
	public ParentOptionVO entityVO(ParentOption parentOption) {
		ParentOptionVO parentOptionVO = BeanUtil.copy(parentOption, ParentOptionVO.class);


		return parentOptionVO;
	}

}
