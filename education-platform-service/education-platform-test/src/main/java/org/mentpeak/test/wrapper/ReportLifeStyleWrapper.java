package org.mentpeak.test.wrapper;

import lombok.AllArgsConstructor;
import org.mentpeak.core.mybatisplus.support.BaseEntityWrapper;
import org.mentpeak.core.tool.utils.BeanUtil;
import org.mentpeak.test.entity.ReportLifeStyle;
import org.mentpeak.test.vo.ReportLifeStyleVO;

/**
 * 生活方式（是否与父母生活）包装类,返回视图层所需的字段
 *
 * @author lxp
 * @since 2022-07-12
 */
@AllArgsConstructor
public class ReportLifeStyleWrapper extends BaseEntityWrapper<ReportLifeStyle, ReportLifeStyleVO>  {


	@Override
	public ReportLifeStyleVO entityVO(ReportLifeStyle reportLifeStyle) {
		ReportLifeStyleVO reportLifeStyleVO = BeanUtil.copy(reportLifeStyle, ReportLifeStyleVO.class);


		return reportLifeStyleVO;
	}

}
