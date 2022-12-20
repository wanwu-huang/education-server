package org.mentpeak.test.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.mentpeak.core.mybatisplus.base.BaseService;
import org.mentpeak.test.entity.TestFollowLevel;
import org.mentpeak.test.vo.TestFollowLevelVO;

/**
 * 关注等级
 服务类
 *
 * @author lxp
 * @since 2022-07-12
 */
public interface ITestFollowLevelService extends BaseService<TestFollowLevel> {

	/**
	 * 自定义分页
	 *
	 * @param page
	 * @param testFollowLevel
	 * @return
	 */
	IPage<TestFollowLevelVO> selectTestFollowLevelPage(IPage<TestFollowLevelVO> page, TestFollowLevelVO testFollowLevel);

}
