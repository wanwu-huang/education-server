package org.mentpeak.test.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.mentpeak.core.mybatisplus.base.BaseService;
import org.mentpeak.test.entity.TestTeacherConclusion;
import org.mentpeak.test.vo.TestTeacherConclusionVO;

/**
 * 教师他评结论 服务类
 *
 * @author lxp
 * @since 2022-07-12
 */
public interface ITestTeacherConclusionService extends BaseService<TestTeacherConclusion> {

	/**
	 * 自定义分页
	 *
	 * @param page
	 * @param testTeacherConclusion
	 * @return
	 */
	IPage<TestTeacherConclusionVO> selectTestTeacherConclusionPage(IPage<TestTeacherConclusionVO> page, TestTeacherConclusionVO testTeacherConclusion);

}
