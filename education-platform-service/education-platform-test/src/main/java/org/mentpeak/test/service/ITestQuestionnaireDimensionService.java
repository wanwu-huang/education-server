package org.mentpeak.test.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.mentpeak.core.mybatisplus.base.BaseService;
import org.mentpeak.test.entity.TestQuestionnaireDimension;
import org.mentpeak.test.vo.TestQuestionnaireDimensionVO;

/**
 * 问卷维度关联表 服务类
 *
 * @author lxp
 * @since 2022-07-12
 */
public interface ITestQuestionnaireDimensionService extends BaseService<TestQuestionnaireDimension> {

	/**
	 * 自定义分页
	 *
	 * @param page
	 * @param testQuestionnaireDimension
	 * @return
	 */
	IPage<TestQuestionnaireDimensionVO> selectTestQuestionnaireDimensionPage(IPage<TestQuestionnaireDimensionVO> page, TestQuestionnaireDimensionVO testQuestionnaireDimension);

}
