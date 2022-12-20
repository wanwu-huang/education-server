package org.mentpeak.test.wrapper;

import lombok.AllArgsConstructor;
import org.mentpeak.core.mybatisplus.support.BaseEntityWrapper;
import org.mentpeak.core.tool.utils.BeanUtil;
import org.mentpeak.test.entity.ReportFamilyStructure;
import org.mentpeak.test.vo.ReportFamilyStructureVO;

/**
 * 家庭结构包装类,返回视图层所需的字段
 *
 * @author lxp
 * @since 2022-07-12
 */
@AllArgsConstructor
public class ReportFamilyStructureWrapper extends BaseEntityWrapper<ReportFamilyStructure, ReportFamilyStructureVO>  {


	@Override
	public ReportFamilyStructureVO entityVO(ReportFamilyStructure reportFamilyStructure) {
		ReportFamilyStructureVO reportFamilyStructureVO = BeanUtil.copy(reportFamilyStructure, ReportFamilyStructureVO.class);


		return reportFamilyStructureVO;
	}

}
