package org.mentpeak.test.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.mentpeak.test.entity.TestParentConclusion;
import org.mentpeak.test.vo.TestParentConclusionVO;

import java.util.List;

/**
 * 家长他评结论 Mapper 接口
 *
 * @author lxp
 * @since 2022-07-12
 */
public interface TestParentConclusionMapper extends BaseMapper<TestParentConclusion> {

	/**
	 * 自定义分页
	 *
	 * @param page
	 * @param testParentConclusion
	 * @return
	 */
	List<TestParentConclusionVO> selectTestParentConclusionPage(IPage page, TestParentConclusionVO testParentConclusion);

}
