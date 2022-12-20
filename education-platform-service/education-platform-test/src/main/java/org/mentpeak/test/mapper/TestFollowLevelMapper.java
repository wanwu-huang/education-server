package org.mentpeak.test.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.mentpeak.test.entity.TestFollowLevel;
import org.mentpeak.test.vo.TestFollowLevelVO;

import java.util.List;

/**
 * 关注等级
 Mapper 接口
 *
 * @author lxp
 * @since 2022-07-12
 */
public interface TestFollowLevelMapper extends BaseMapper<TestFollowLevel> {

	/**
	 * 自定义分页
	 *
	 * @param page
	 * @param testFollowLevel
	 * @return
	 */
	List<TestFollowLevelVO> selectTestFollowLevelPage(IPage page, TestFollowLevelVO testFollowLevel);

}
