package org.mentpeak.parent.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.mentpeak.parent.entity.ParentQuestion;
import org.mentpeak.parent.vo.ParentQuestionVO;

import java.util.List;

/**
 * 家长他评问卷题目信息表 Mapper 接口
 *
 * @author lxp
 * @since 2022-06-17
 */
public interface ParentQuestionMapper extends BaseMapper<ParentQuestion> {

	/**
	 * 自定义分页
	 *
	 * @param page
	 * @param parentQuestion
	 * @return
	 */
	List<ParentQuestionVO> selectParentQuestionPage(IPage page, ParentQuestionVO parentQuestion);

}
