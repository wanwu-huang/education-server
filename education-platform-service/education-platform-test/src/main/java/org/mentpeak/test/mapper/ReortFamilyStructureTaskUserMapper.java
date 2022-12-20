package org.mentpeak.test.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.mentpeak.test.entity.ReortFamilyStructureTaskUser;
import org.mentpeak.test.vo.ReortFamilyStructureTaskUserVO;

import java.util.List;

/**
 * 家庭结构任务用户关联表 Mapper 接口
 *
 * @author lxp
 * @since 2022-07-12
 */
public interface ReortFamilyStructureTaskUserMapper extends BaseMapper<ReortFamilyStructureTaskUser> {

	/**
	 * 自定义分页
	 *
	 * @param page
	 * @param reortFamilyStructureTaskUser
	 * @return
	 */
	List<ReortFamilyStructureTaskUserVO> selectReortFamilyStructureTaskUserPage(IPage page, ReortFamilyStructureTaskUserVO reortFamilyStructureTaskUser);

}
