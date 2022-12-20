package org.mentpeak.parent.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.mentpeak.parent.entity.ParentOption;
import org.mentpeak.parent.vo.ParentOptionVO;

import java.util.List;

/**
 * 家长他评问卷题支信息表 Mapper 接口
 *
 * @author lxp
 * @since 2022-06-17
 */
public interface ParentOptionMapper extends BaseMapper<ParentOption> {

	/**
	 * 自定义分页
	 *
	 * @param page
	 * @param parentOption
	 * @return
	 */
	List<ParentOptionVO> selectParentOptionPage(IPage page, ParentOptionVO parentOption);

}
