package org.mentpeak.test.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.mentpeak.test.entity.ReportLifeStyle;
import org.mentpeak.test.vo.ReportLifeStyleVO;

import java.util.List;

/**
 * 生活方式（是否与父母生活） Mapper 接口
 *
 * @author lxp
 * @since 2022-07-12
 */
public interface ReportLifeStyleMapper extends BaseMapper<ReportLifeStyle> {

	/**
	 * 自定义分页
	 *
	 * @param page
	 * @param reportLifeStyle
	 * @return
	 */
	List<ReportLifeStyleVO> selectReportLifeStylePage(IPage page, ReportLifeStyleVO reportLifeStyle);

}
