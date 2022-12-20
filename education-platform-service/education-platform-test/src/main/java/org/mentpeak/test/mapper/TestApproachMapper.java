package org.mentpeak.test.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.mentpeak.test.entity.TestApproach;
import org.mentpeak.test.vo.TestApproachVO;

import java.util.List;

/**
 * 测评途径表 Mapper 接口
 *
 * @author lxp
 * @since 2022-07-12
 */
public interface TestApproachMapper extends BaseMapper<TestApproach> {

	/**
	 * 自定义分页
	 *
	 * @param page
	 * @param testApproach
	 * @return
	 */
	List<TestApproachVO> selectTestApproachPage(IPage page, TestApproachVO testApproach);

}
