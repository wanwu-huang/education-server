package org.mentpeak.test.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.mentpeak.core.mybatisplus.base.BaseServiceImpl;
import org.mentpeak.test.entity.TestIndex;
import org.mentpeak.test.mapper.TestIndexMapper;
import org.mentpeak.test.service.ITestIndexService;
import org.mentpeak.test.vo.TestIndexVO;
import org.springframework.stereotype.Service;

/**
 * 指标表 服务实现类
 *
 * @author lxp
 * @since 2022-07-12
 */
@Service
public class TestIndexServiceImpl extends BaseServiceImpl<TestIndexMapper, TestIndex> implements ITestIndexService {

	@Override
	public IPage<TestIndexVO> selectTestIndexPage(IPage<TestIndexVO> page, TestIndexVO testIndex) {
		return page.setRecords(baseMapper.selectTestIndexPage(page, testIndex));
	}

}
