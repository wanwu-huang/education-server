package org.mentpeak.test.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.mentpeak.test.entity.GradeClass;
import org.mentpeak.test.vo.GradeClassVO;

import java.util.List;

/**
 * 年级-班级对应关联表 Mapper 接口
 *
 * @author lxp
 * @since 2022-08-12
 */
public interface GradeClassMapper extends BaseMapper<GradeClass> {

	/**
	 * 自定义分页
	 *
	 * @param page
	 * @param gradeClass
	 * @return
	 */
	List<GradeClassVO> selectGradeClassPage(IPage page, GradeClassVO gradeClass);

	/**
	 * 删除年级班级
	 * @param ids
	 * @return
	 */
	int deleteByGradeId(List<Long> ids);
}
