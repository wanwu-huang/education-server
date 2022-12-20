package org.mentpeak.test.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.mentpeak.core.mybatisplus.base.BaseServiceImpl;
import org.mentpeak.test.entity.TestApproach;
import org.mentpeak.test.mapper.TestApproachMapper;
import org.mentpeak.test.service.ITestApproachService;
import org.mentpeak.test.vo.TestApproachVO;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 测评途径表 服务实现类
 *
 * @author lxp
 * @since 2022-07-12
 */
@Service
public class TestApproachServiceImpl extends BaseServiceImpl<TestApproachMapper, TestApproach> implements ITestApproachService {

	@Override
	public IPage<TestApproachVO> selectTestApproachPage(IPage<TestApproachVO> page, TestApproachVO testApproach) {
		return page.setRecords(baseMapper.selectTestApproachPage(page, testApproach));
	}

	@Override
	public List<TestApproach> testApproachList() {
		return baseMapper.selectList(Wrappers.<TestApproach>lambdaQuery()
				.eq(TestApproach::getStatus,1).orderByAsc(TestApproach::getSort));
	}
}
