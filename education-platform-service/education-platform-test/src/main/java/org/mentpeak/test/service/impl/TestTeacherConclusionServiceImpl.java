package org.mentpeak.test.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.mentpeak.core.mybatisplus.base.BaseServiceImpl;
import org.mentpeak.test.entity.TestTeacherConclusion;
import org.mentpeak.test.mapper.TestTeacherConclusionMapper;
import org.mentpeak.test.service.ITestTeacherConclusionService;
import org.mentpeak.test.vo.TestTeacherConclusionVO;
import org.springframework.stereotype.Service;

/**
 * 教师他评结论 服务实现类
 *
 * @author lxp
 * @since 2022-07-12
 */
@Service
public class TestTeacherConclusionServiceImpl extends BaseServiceImpl<TestTeacherConclusionMapper, TestTeacherConclusion> implements ITestTeacherConclusionService {

	@Override
	public IPage<TestTeacherConclusionVO> selectTestTeacherConclusionPage(IPage<TestTeacherConclusionVO> page, TestTeacherConclusionVO testTeacherConclusion) {
		return page.setRecords(baseMapper.selectTestTeacherConclusionPage(page, testTeacherConclusion));
	}

}
