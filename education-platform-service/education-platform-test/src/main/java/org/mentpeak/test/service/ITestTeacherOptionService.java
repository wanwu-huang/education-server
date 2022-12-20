package org.mentpeak.test.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.mentpeak.core.mybatisplus.base.BaseService;
import org.mentpeak.test.entity.TestTeacherOption;
import org.mentpeak.test.vo.TestTeacherOptionVO;

/**
 * 教师评定题支信息表 服务类
 *
 * @author lxp
 * @since 2022-08-15
 */
public interface ITestTeacherOptionService extends BaseService<TestTeacherOption> {

	/**
	 * 自定义分页
	 *
	 * @param page
	 * @param testTeacherOption
	 * @return
	 */
	IPage<TestTeacherOptionVO> selectTestTeacherOptionPage(IPage<TestTeacherOptionVO> page, TestTeacherOptionVO testTeacherOption);

}
