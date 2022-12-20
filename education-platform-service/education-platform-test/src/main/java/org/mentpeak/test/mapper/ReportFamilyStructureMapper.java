package org.mentpeak.test.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.mentpeak.test.entity.ReportFamilyStructure;
import org.mentpeak.test.vo.ReportFamilyStructureVO;

import java.util.List;

/**
 * 家庭结构 Mapper 接口
 *
 * @author lxp
 * @since 2022-07-12
 */
public interface ReportFamilyStructureMapper extends BaseMapper<ReportFamilyStructure> {

	/**
	 * 自定义分页
	 *
	 * @param page
	 * @param reportFamilyStructure
	 * @return
	 */
	List<ReportFamilyStructureVO> selectReportFamilyStructurePage(IPage page, ReportFamilyStructureVO reportFamilyStructure);

}
