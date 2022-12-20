package org.mentpeak.resourcemanager.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.mentpeak.core.mybatisplus.base.BaseServiceImpl;
import org.mentpeak.resourcemanager.entity.ParentQuestion;
import org.mentpeak.resourcemanager.mapper.ParentQuestionMapper;
import org.mentpeak.resourcemanager.service.IParentQuestionService;
import org.mentpeak.resourcemanager.vo.ParentQuestionVO;
import org.springframework.stereotype.Service;

/**
 * 家长他评问卷题目信息表 服务实现类
 *
 * @author lxp
 * @since 2022-06-17
 */
@Service
public class ParentQuestionServiceImpl extends BaseServiceImpl<ParentQuestionMapper, ParentQuestion> implements IParentQuestionService {

	@Override
	public IPage<ParentQuestionVO> selectParentQuestionPage(IPage<ParentQuestionVO> page, ParentQuestionVO parentQuestion) {
		return page.setRecords(baseMapper.selectParentQuestionPage(page, parentQuestion));
	}

}
