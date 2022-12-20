package org.mentpeak.test.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.mentpeak.test.entity.ReportLifeStyleTaskUser;
import org.mentpeak.test.vo.ReportLifeStyleTaskUserVO;

import java.util.List;

/**
 * 生活方式任务用户关联表 Mapper 接口
 *
 * @author lxp
 * @since 2022-07-12
 */
public interface ReportLifeStyleTaskUserMapper extends BaseMapper<ReportLifeStyleTaskUser> {

	/**
	 * 自定义分页
	 *
	 * @param page
	 * @param reportLifeStyleTaskUser
	 * @return
	 */
	List<ReportLifeStyleTaskUserVO> selectReportLifeStyleTaskUserPage(IPage page, ReportLifeStyleTaskUserVO reportLifeStyleTaskUser);

}
