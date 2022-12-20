package org.mentpeak.test.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.mentpeak.core.mybatisplus.base.BaseService;
import org.mentpeak.test.entity.ReportLifeStyle;
import org.mentpeak.test.vo.ReportLifeStyleVO;

/**
 * 生活方式（是否与父母生活） 服务类
 *
 * @author lxp
 * @since 2022-07-12
 */
public interface IReportLifeStyleService extends BaseService<ReportLifeStyle> {

	/**
	 * 自定义分页
	 *
	 * @param page
	 * @param reportLifeStyle
	 * @return
	 */
	IPage<ReportLifeStyleVO> selectReportLifeStylePage(IPage<ReportLifeStyleVO> page, ReportLifeStyleVO reportLifeStyle);

}
