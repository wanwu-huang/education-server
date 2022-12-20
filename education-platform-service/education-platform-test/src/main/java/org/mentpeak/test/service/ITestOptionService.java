package org.mentpeak.test.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.mentpeak.core.mybatisplus.base.BaseService;
import org.mentpeak.test.entity.TestOption;
import org.mentpeak.test.vo.TestOptionVO;

/**
 * 题支信息表 服务类
 *
 * @author lxp
 * @since 2022-07-12
 */
public interface ITestOptionService extends BaseService<TestOption> {

	/**
	 * 自定义分页
	 *
	 * @param page
	 * @param testOption
	 * @return
	 */
	IPage<TestOptionVO> selectTestOptionPage(IPage<TestOptionVO> page, TestOptionVO testOption);

}
