package org.mentpeak.test.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Param;
import org.mentpeak.test.entity.TeacherClass;
import org.mentpeak.test.vo.TeacherClassVO;

import java.util.List;

/**
 * 班主任班级关联表 Mapper 接口
 *
 * @author lxp
 * @since 2022-07-12
 */
public interface TeacherClassMapper extends BaseMapper<TeacherClass> {

	/**
	 * 自定义分页
	 *
	 * @param page
	 * @param teacherClass
	 * @return
	 */
	List<TeacherClassVO> selectTeacherClassPage(IPage page, TeacherClassVO teacherClass);

	/**
	 * 根据年级删除关联关系数据
	 * @param gradeId
	 * @return
	 */
	int deleteByGradeId(@Param("gradeId") Long gradeId);
}
