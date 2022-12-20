package org.mentpeak.test.wrapper;

import lombok.AllArgsConstructor;
import org.mentpeak.core.mybatisplus.support.BaseEntityWrapper;
import org.mentpeak.core.tool.utils.BeanUtil;
import org.mentpeak.test.entity.ReortFamilyStructureTaskUser;
import org.mentpeak.test.vo.ReortFamilyStructureTaskUserVO;

/**
 * 家庭结构任务用户关联表包装类,返回视图层所需的字段
 *
 * @author lxp
 * @since 2022-07-12
 */
@AllArgsConstructor
public class ReortFamilyStructureTaskUserWrapper extends BaseEntityWrapper<ReortFamilyStructureTaskUser, ReortFamilyStructureTaskUserVO>  {


	@Override
	public ReortFamilyStructureTaskUserVO entityVO(ReortFamilyStructureTaskUser reortFamilyStructureTaskUser) {
		ReortFamilyStructureTaskUserVO reortFamilyStructureTaskUserVO = BeanUtil.copy(reortFamilyStructureTaskUser, ReortFamilyStructureTaskUserVO.class);


		return reortFamilyStructureTaskUserVO;
	}

}
