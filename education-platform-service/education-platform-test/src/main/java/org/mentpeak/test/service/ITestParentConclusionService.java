package org.mentpeak.test.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.mentpeak.core.mybatisplus.base.BaseService;
import org.mentpeak.test.entity.TestParentConclusion;
import org.mentpeak.test.vo.TestParentConclusionVO;

/**
 * 家长他评结论 服务类
 *
 * @author lxp
 * @since 2022-07-12
 */
public interface ITestParentConclusionService extends BaseService<TestParentConclusion> {

	/**
	 * 自定义分页
	 *
	 * @param page
	 * @param testParentConclusion
	 * @return
	 */
	IPage<TestParentConclusionVO> selectTestParentConclusionPage(IPage<TestParentConclusionVO> page, TestParentConclusionVO testParentConclusion);

}
