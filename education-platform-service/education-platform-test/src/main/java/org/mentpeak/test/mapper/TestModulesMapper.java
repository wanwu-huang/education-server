package org.mentpeak.test.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.mentpeak.test.entity.TestModules;
import org.mentpeak.test.vo.TestModulesVO;

import java.util.List;

/**
 * 问卷模块表 Mapper 接口
 *
 * @author lxp
 * @since 2022-07-12
 */
public interface TestModulesMapper extends BaseMapper<TestModules> {

	/**
	 * 自定义分页
	 *
	 * @param page
	 * @param testModules
	 * @return
	 */
	List<TestModulesVO> selectTestModulesPage(IPage page, TestModulesVO testModules);

}
