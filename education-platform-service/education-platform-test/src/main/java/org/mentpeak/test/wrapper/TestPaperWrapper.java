package org.mentpeak.test.wrapper;

import lombok.AllArgsConstructor;
import org.mentpeak.core.mybatisplus.support.BaseEntityWrapper;
import org.mentpeak.core.tool.utils.BeanUtil;
import org.mentpeak.test.entity.TestPaper;
import org.mentpeak.test.vo.TestPaperVO;

/**
 * 用户试卷信息表包装类,返回视图层所需的字段
 *
 * @author lxp
 * @since 2022-07-12
 */
@AllArgsConstructor
public class TestPaperWrapper extends BaseEntityWrapper<TestPaper, TestPaperVO>  {


	@Override
	public TestPaperVO entityVO(TestPaper testPaper) {
		TestPaperVO testPaperVO = BeanUtil.copy(testPaper, TestPaperVO.class);


		return testPaperVO;
	}

}
