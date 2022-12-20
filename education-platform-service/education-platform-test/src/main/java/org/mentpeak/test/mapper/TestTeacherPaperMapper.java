package org.mentpeak.test.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.mentpeak.test.entity.TestTeacherPaper;
import org.mentpeak.test.vo.TeacherPaperVO;
import org.mentpeak.test.vo.TeacherStudentVO;
import org.mentpeak.test.vo.TestTeacherPaperVO;

import java.util.List;

/**
 * 教师评定试卷信息表 Mapper 接口
 *
 * @author lxp
 * @since 2022-08-15
 */
public interface TestTeacherPaperMapper extends BaseMapper<TestTeacherPaper> {

	/**
	 * 自定义分页
	 *
	 * @param page
	 * @param testTeacherPaper
	 * @return
	 */
	List<TestTeacherPaperVO> selectTestTeacherPaperPage(IPage page, TestTeacherPaperVO testTeacherPaper);


	/**
	 * 查询老师对应的学生信息
	 * @param teacherId
	 * @return
	 */
	List<TeacherStudentVO> stuListByTeacherId(@Param("teacherId") Long teacherId);

	/**
	 * 查询指定年级班级下对应的学生信息
	 * @param gradeId
	 * @param classId
	 * @return
	 */
	List<TeacherStudentVO> stuListByGradeClassId(@Param("gradeId") Long gradeId,@Param("classId") Long classId);

	/**
	 * 评定列表
	 * @param page
	 * @param userId
	 * @param gradeId
	 * @param classId
	 * @param status
	 * @return
	 */
	Page<TeacherPaperVO> teacherRatingList(IPage<TeacherPaperVO> page, @Param("userId") Long userId,@Param("gradeId") Long gradeId, @Param("classId") Long classId,
			@Param("status") Integer status);
}
