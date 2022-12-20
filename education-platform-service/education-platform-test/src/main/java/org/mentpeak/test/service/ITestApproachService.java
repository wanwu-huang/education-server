package org.mentpeak.test.service;

import com.alicp.jetcache.anno.Cached;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.mentpeak.core.mybatisplus.base.BaseService;
import org.mentpeak.test.entity.TestApproach;
import org.mentpeak.test.vo.TestApproachVO;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 测评途径表 服务类
 *
 * @author lxp
 * @since 2022-07-12
 */
public interface ITestApproachService extends BaseService<TestApproach> {

	/**
	 * 自定义分页
	 *
	 * @param page
	 * @param testApproach
	 * @return
	 */
	IPage<TestApproachVO> selectTestApproachPage(IPage<TestApproachVO> page, TestApproachVO testApproach);


	/**
	 * 测评途径列表
	 * @return list
	 */
	@Cached(name = "testApproach:list",expire = 2,timeUnit = TimeUnit.HOURS)
	List<TestApproach> testApproachList();
}
