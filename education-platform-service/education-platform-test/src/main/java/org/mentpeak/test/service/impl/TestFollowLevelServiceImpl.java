package org.mentpeak.test.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.mentpeak.core.mybatisplus.base.BaseServiceImpl;
import org.mentpeak.test.entity.TestFollowLevel;
import org.mentpeak.test.mapper.TestFollowLevelMapper;
import org.mentpeak.test.service.ITestFollowLevelService;
import org.mentpeak.test.vo.TestFollowLevelVO;
import org.springframework.stereotype.Service;

/**
 * 关注等级
 服务实现类
 *
 * @author lxp
 * @since 2022-07-12
 */
@Service
public class TestFollowLevelServiceImpl extends BaseServiceImpl<TestFollowLevelMapper, TestFollowLevel> implements ITestFollowLevelService {

	@Override
	public IPage<TestFollowLevelVO> selectTestFollowLevelPage(IPage<TestFollowLevelVO> page, TestFollowLevelVO testFollowLevel) {
		return page.setRecords(baseMapper.selectTestFollowLevelPage(page, testFollowLevel));
	}

}
