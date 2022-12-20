package org.mentpeak.test.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.mentpeak.core.mybatisplus.base.BaseServiceImpl;
import org.mentpeak.test.entity.TestTeacherOption;
import org.mentpeak.test.mapper.TestTeacherOptionMapper;
import org.mentpeak.test.service.ITestTeacherOptionService;
import org.mentpeak.test.vo.TestTeacherOptionVO;
import org.springframework.stereotype.Service;

/**
 * 教师评定题支信息表 服务实现类
 *
 * @author lxp
 * @since 2022-08-15
 */
@Service
public class TestTeacherOptionServiceImpl extends BaseServiceImpl<TestTeacherOptionMapper, TestTeacherOption> implements ITestTeacherOptionService {

	@Override
	public IPage<TestTeacherOptionVO> selectTestTeacherOptionPage(IPage<TestTeacherOptionVO> page, TestTeacherOptionVO testTeacherOption) {
		return page.setRecords(baseMapper.selectTestTeacherOptionPage(page, testTeacherOption));
	}

}
