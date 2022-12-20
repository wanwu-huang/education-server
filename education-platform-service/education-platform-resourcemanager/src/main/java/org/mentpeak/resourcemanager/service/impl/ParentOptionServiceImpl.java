package org.mentpeak.resourcemanager.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.mentpeak.core.mybatisplus.base.BaseServiceImpl;
import org.mentpeak.resourcemanager.entity.ParentOption;
import org.mentpeak.resourcemanager.mapper.ParentOptionMapper;
import org.mentpeak.resourcemanager.service.IParentOptionService;
import org.mentpeak.resourcemanager.vo.ParentOptionVO;
import org.springframework.stereotype.Service;

/**
 * 家长他评问卷题支信息表 服务实现类
 *
 * @author lxp
 * @since 2022-06-17
 */
@Service
public class ParentOptionServiceImpl extends BaseServiceImpl<ParentOptionMapper, ParentOption> implements IParentOptionService {

	@Override
	public IPage<ParentOptionVO> selectParentOptionPage(IPage<ParentOptionVO> page, ParentOptionVO parentOption) {
		return page.setRecords(baseMapper.selectParentOptionPage(page, parentOption));
	}

}
