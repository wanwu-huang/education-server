package org.mentpeak.parent.wrapper;

import lombok.AllArgsConstructor;
import org.mentpeak.core.mybatisplus.support.BaseEntityWrapper;
import org.mentpeak.core.tool.utils.BeanUtil;
import org.mentpeak.parent.entity.Letters;
import org.mentpeak.parent.vo.LettersVO;

/**
 * 家长端-匿名信表包装类,返回视图层所需的字段
 *
 * @author lxp
 * @since 2022-06-15
 */
@AllArgsConstructor
public class LettersWrapper extends BaseEntityWrapper<Letters, LettersVO>  {


	@Override
	public LettersVO entityVO(Letters letters) {
		LettersVO lettersVO = BeanUtil.copy(letters, LettersVO.class);


		return lettersVO;
	}

}
