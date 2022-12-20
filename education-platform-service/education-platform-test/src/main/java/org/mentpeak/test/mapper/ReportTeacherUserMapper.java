package org.mentpeak.test.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.mentpeak.test.entity.ReportTeacherUser;
import org.mentpeak.test.vo.ReportTeacherUserVO;

import java.util.List;

/**
 * 老师用户报告表 Mapper 接口
 *
 * @author lxp
 * @since 2022-07-12
 */
public interface ReportTeacherUserMapper extends BaseMapper<ReportTeacherUser> {

	/**
	 * 自定义分页
	 *
	 * @param page
	 * @param reportTeacherUser
	 * @return
	 */
	List<ReportTeacherUserVO> selectReportTeacherUserPage(IPage page, ReportTeacherUserVO reportTeacherUser);

}
