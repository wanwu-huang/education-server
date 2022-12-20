package org.mentpeak.test.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.mentpeak.test.entity.TestQuestionnaireDimension;
import org.mentpeak.test.vo.TestQuestionnaireDimensionVO;

import java.util.List;

/**
 * 问卷维度关联表 Mapper 接口
 *
 * @author lxp
 * @since 2022-07-12
 */
public interface TestQuestionnaireDimensionMapper extends BaseMapper<TestQuestionnaireDimension> {

	/**
	 * 自定义分页
	 *
	 * @param page
	 * @param testQuestionnaireDimension
	 * @return
	 */
	List<TestQuestionnaireDimensionVO> selectTestQuestionnaireDimensionPage(IPage page, TestQuestionnaireDimensionVO testQuestionnaireDimension);

}
