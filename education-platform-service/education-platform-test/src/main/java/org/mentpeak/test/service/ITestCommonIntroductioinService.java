package org.mentpeak.test.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.mentpeak.core.mybatisplus.base.BaseService;
import org.mentpeak.test.entity.TestCommonIntroductioin;
import org.mentpeak.test.vo.TestCommonIntroductioinVO;

/**
 * 固定文本 服务类
 *
 * @author lxp
 * @since 2022-07-12
 */
public interface ITestCommonIntroductioinService extends BaseService<TestCommonIntroductioin> {

	/**
	 * 自定义分页
	 *
	 * @param page
	 * @param testCommonIntroductioin
	 * @return
	 */
	IPage<TestCommonIntroductioinVO> selectTestCommonIntroductioinPage(IPage<TestCommonIntroductioinVO> page, TestCommonIntroductioinVO testCommonIntroductioin);

}
