package org.mentpeak.test.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.mentpeak.core.mybatisplus.base.BaseServiceImpl;
import org.mentpeak.test.entity.TestCommonIntroductioin;
import org.mentpeak.test.mapper.TestCommonIntroductioinMapper;
import org.mentpeak.test.service.ITestCommonIntroductioinService;
import org.mentpeak.test.vo.TestCommonIntroductioinVO;
import org.springframework.stereotype.Service;

/**
 * 固定文本 服务实现类
 *
 * @author lxp
 * @since 2022-07-12
 */
@Service
public class TestCommonIntroductioinServiceImpl extends BaseServiceImpl<TestCommonIntroductioinMapper, TestCommonIntroductioin> implements ITestCommonIntroductioinService {

	@Override
	public IPage<TestCommonIntroductioinVO> selectTestCommonIntroductioinPage(IPage<TestCommonIntroductioinVO> page, TestCommonIntroductioinVO testCommonIntroductioin) {
		return page.setRecords(baseMapper.selectTestCommonIntroductioinPage(page, testCommonIntroductioin));
	}

}
