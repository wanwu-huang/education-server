package org.mentpeak.test.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.mentpeak.core.mybatisplus.base.BaseService;
import org.mentpeak.test.entity.ReportFamilyStructure;
import org.mentpeak.test.vo.ReportFamilyStructureVO;

/**
 * 家庭结构 服务类
 *
 * @author lxp
 * @since 2022-07-12
 */
public interface IReportFamilyStructureService extends BaseService<ReportFamilyStructure> {

	/**
	 * 自定义分页
	 *
	 * @param page
	 * @param reportFamilyStructure
	 * @return
	 */
	IPage<ReportFamilyStructureVO> selectReportFamilyStructurePage(IPage<ReportFamilyStructureVO> page, ReportFamilyStructureVO reportFamilyStructure);

}
