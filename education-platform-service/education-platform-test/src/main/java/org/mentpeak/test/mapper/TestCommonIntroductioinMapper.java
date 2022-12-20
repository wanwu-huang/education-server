package org.mentpeak.test.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.mentpeak.test.entity.TestCommonIntroductioin;
import org.mentpeak.test.vo.TestCommonIntroductioinVO;

import java.util.List;

/**
 * 固定文本 Mapper 接口
 *
 * @author lxp
 * @since 2022-07-12
 */
public interface TestCommonIntroductioinMapper extends BaseMapper<TestCommonIntroductioin> {

	/**
	 * 自定义分页
	 *
	 * @param page
	 * @param testCommonIntroductioin
	 * @return
	 */
	List<TestCommonIntroductioinVO> selectTestCommonIntroductioinPage(IPage page, TestCommonIntroductioinVO testCommonIntroductioin);

}
