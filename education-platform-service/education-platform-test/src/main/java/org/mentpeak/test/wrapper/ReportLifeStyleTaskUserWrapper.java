package org.mentpeak.test.wrapper;

import lombok.AllArgsConstructor;
import org.mentpeak.core.mybatisplus.support.BaseEntityWrapper;
import org.mentpeak.core.tool.utils.BeanUtil;
import org.mentpeak.test.entity.ReportLifeStyleTaskUser;
import org.mentpeak.test.vo.ReportLifeStyleTaskUserVO;

/**
 * 生活方式任务用户关联表包装类,返回视图层所需的字段
 *
 * @author lxp
 * @since 2022-07-12
 */
@AllArgsConstructor
public class ReportLifeStyleTaskUserWrapper extends BaseEntityWrapper<ReportLifeStyleTaskUser, ReportLifeStyleTaskUserVO>  {


	@Override
	public ReportLifeStyleTaskUserVO entityVO(ReportLifeStyleTaskUser reportLifeStyleTaskUser) {
		ReportLifeStyleTaskUserVO reportLifeStyleTaskUserVO = BeanUtil.copy(reportLifeStyleTaskUser, ReportLifeStyleTaskUserVO.class);


		return reportLifeStyleTaskUserVO;
	}

}
