package org.mentpeak.test.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.mentpeak.test.entity.TestTeacherOption;
import org.mentpeak.test.vo.TestTeacherOptionVO;

import java.util.List;

/**
 * 教师评定题支信息表 Mapper 接口
 *
 * @author lxp
 * @since 2022-08-15
 */
public interface TestTeacherOptionMapper extends BaseMapper<TestTeacherOption> {

	/**
	 * 自定义分页
	 *
	 * @param page
	 * @param testTeacherOption
	 * @return
	 */
	List<TestTeacherOptionVO> selectTestTeacherOptionPage(IPage page, TestTeacherOptionVO testTeacherOption);

}
