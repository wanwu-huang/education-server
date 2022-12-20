package org.mentpeak.parent.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.mentpeak.core.mybatisplus.base.BaseService;
import org.mentpeak.parent.entity.ParentOption;
import org.mentpeak.parent.vo.ParentOptionVO;

/**
 * 家长他评问卷题支信息表 服务类
 *
 * @author lxp
 * @since 2022-06-17
 */
public interface IParentOptionService extends BaseService<ParentOption> {

	/**
	 * 自定义分页
	 *
	 * @param page
	 * @param parentOption
	 * @return
	 */
	IPage<ParentOptionVO> selectParentOptionPage(IPage<ParentOptionVO> page, ParentOptionVO parentOption);

}
