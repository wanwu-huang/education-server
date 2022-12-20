package org.mentpeak.test.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.mentpeak.core.mybatisplus.base.BaseService;
import org.mentpeak.test.entity.ReortFamilyStructureTaskUser;
import org.mentpeak.test.vo.ReortFamilyStructureTaskUserVO;

/**
 * 家庭结构任务用户关联表 服务类
 *
 * @author lxp
 * @since 2022-07-12
 */
public interface IReortFamilyStructureTaskUserService extends BaseService<ReortFamilyStructureTaskUser> {

	/**
	 * 自定义分页
	 *
	 * @param page
	 * @param reortFamilyStructureTaskUser
	 * @return
	 */
	IPage<ReortFamilyStructureTaskUserVO> selectReortFamilyStructureTaskUserPage(IPage<ReortFamilyStructureTaskUserVO> page, ReortFamilyStructureTaskUserVO reortFamilyStructureTaskUser);

}
