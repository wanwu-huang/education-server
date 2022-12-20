package org.mentpeak.test.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.mentpeak.core.mybatisplus.base.BaseService;
import org.mentpeak.test.entity.ReportLifeStyleTaskUser;
import org.mentpeak.test.vo.ReportLifeStyleTaskUserVO;

/**
 * 生活方式任务用户关联表 服务类
 *
 * @author lxp
 * @since 2022-07-12
 */
public interface IReportLifeStyleTaskUserService extends BaseService<ReportLifeStyleTaskUser> {

	/**
	 * 自定义分页
	 *
	 * @param page
	 * @param reportLifeStyleTaskUser
	 * @return
	 */
	IPage<ReportLifeStyleTaskUserVO> selectReportLifeStyleTaskUserPage(IPage<ReportLifeStyleTaskUserVO> page, ReportLifeStyleTaskUserVO reportLifeStyleTaskUser);

}
