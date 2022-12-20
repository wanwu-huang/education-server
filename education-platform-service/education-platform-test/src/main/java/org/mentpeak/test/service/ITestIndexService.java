package org.mentpeak.test.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.mentpeak.core.mybatisplus.base.BaseService;
import org.mentpeak.test.entity.TestIndex;
import org.mentpeak.test.vo.TestIndexVO;

/**
 * 指标表 服务类
 *
 * @author lxp
 * @since 2022-07-12
 */
public interface ITestIndexService extends BaseService<TestIndex> {

	/**
	 * 自定义分页
	 *
	 * @param page
	 * @param testIndex
	 * @return
	 */
	IPage<TestIndexVO> selectTestIndexPage(IPage<TestIndexVO> page, TestIndexVO testIndex);

}
