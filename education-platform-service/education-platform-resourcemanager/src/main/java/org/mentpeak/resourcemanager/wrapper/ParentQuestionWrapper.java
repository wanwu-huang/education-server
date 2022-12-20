package org.mentpeak.resourcemanager.wrapper;

import lombok.AllArgsConstructor;
import org.mentpeak.core.mybatisplus.support.BaseEntityWrapper;
import org.mentpeak.core.tool.utils.BeanUtil;
import org.mentpeak.resourcemanager.entity.ParentQuestion;
import org.mentpeak.resourcemanager.vo.ParentQuestionVO;

/**
 * 家长他评问卷题目信息表包装类,返回视图层所需的字段
 *
 * @author lxp
 * @since 2022-06-17
 */
@AllArgsConstructor
public class ParentQuestionWrapper extends BaseEntityWrapper<ParentQuestion, ParentQuestionVO>  {


	@Override
	public ParentQuestionVO entityVO(ParentQuestion parentQuestion) {
		ParentQuestionVO parentQuestionVO = BeanUtil.copy(parentQuestion, ParentQuestionVO.class);


		return parentQuestionVO;
	}

}
