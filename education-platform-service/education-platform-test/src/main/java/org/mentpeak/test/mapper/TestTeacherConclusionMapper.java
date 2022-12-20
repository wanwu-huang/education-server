package org.mentpeak.test.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.mentpeak.test.entity.TestTeacherConclusion;
import org.mentpeak.test.vo.TestTeacherConclusionVO;

import java.util.List;

/**
 * 教师他评结论 Mapper 接口
 *
 * @author lxp
 * @since 2022-07-12
 */
public interface TestTeacherConclusionMapper extends BaseMapper<TestTeacherConclusion> {

	/**
	 * 自定义分页
	 *
	 * @param page
	 * @param testTeacherConclusion
	 * @return
	 */
	List<TestTeacherConclusionVO> selectTestTeacherConclusionPage(IPage page, TestTeacherConclusionVO testTeacherConclusion);

}
