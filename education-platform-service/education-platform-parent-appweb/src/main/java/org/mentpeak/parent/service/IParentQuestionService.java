package org.mentpeak.parent.service;

import org.mentpeak.core.mybatisplus.base.BaseService;
import org.mentpeak.parent.dto.ParentPaperDTO;
import org.mentpeak.parent.dto.ResultDTO;
import org.mentpeak.parent.entity.ParentQuestion;

import java.util.List;

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
	 * @return 题干列表
	 */
	List<ParentQuestion> selectAllQuestions();

	ParentPaperDTO selectDetail(Long studentId);

	boolean saveResult(ResultDTO resultDTO);
}
