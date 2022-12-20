package org.mentpeak.test.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.mentpeak.core.mybatisplus.base.BaseServiceImpl;
import org.mentpeak.test.entity.ReortFamilyStructureTaskUser;
import org.mentpeak.test.mapper.ReortFamilyStructureTaskUserMapper;
import org.mentpeak.test.service.IReortFamilyStructureTaskUserService;
import org.mentpeak.test.vo.ReortFamilyStructureTaskUserVO;
import org.springframework.stereotype.Service;

/**
 * 家庭结构任务用户关联表 服务实现类
 *
 * @author lxp
 * @since 2022-07-12
 */
@Service
public class ReortFamilyStructureTaskUserServiceImpl extends BaseServiceImpl<ReortFamilyStructureTaskUserMapper, ReortFamilyStructureTaskUser> implements IReortFamilyStructureTaskUserService {

	@Override
	public IPage<ReortFamilyStructureTaskUserVO> selectReortFamilyStructureTaskUserPage(IPage<ReortFamilyStructureTaskUserVO> page, ReortFamilyStructureTaskUserVO reortFamilyStructureTaskUser) {
		return page.setRecords(baseMapper.selectReortFamilyStructureTaskUserPage(page, reortFamilyStructureTaskUser));
	}

}
