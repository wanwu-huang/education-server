package org.mentpeak.resourcemanager.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.mentpeak.core.mybatisplus.base.BaseService;
import org.mentpeak.resourcemanager.entity.ParentQuestion;
import org.mentpeak.resourcemanager.vo.ParentQuestionVO;

/**
 * 家长他评问卷题目信息表 服务类
 *
 * @author lxp
 * @since 2022-06-17
 */
public interface IParentQuestionService extends BaseService<ParentQuestion> {

	/**
	 * 自定义分页
	 *
	 * @param page
	 * @param parentQuestion
	 * @return
	 */
	IPage<ParentQuestionVO> selectParentQuestionPage(IPage<ParentQuestionVO> page, ParentQuestionVO parentQuestion);

}
