package org.mentpeak.test.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.mentpeak.core.mybatisplus.base.BaseService;
import org.mentpeak.test.entity.TeacherClass;
import org.mentpeak.test.vo.TeacherClassVO;

/**
 * 班主任班级关联表 服务类
 *
 * @author lxp
 * @since 2022-07-12
 */
public interface ITeacherClassService extends BaseService<TeacherClass> {

	/**
	 * 自定义分页
	 *
	 * @param page
	 * @param teacherClass
	 * @return
	 */
	IPage<TeacherClassVO> selectTeacherClassPage(IPage<TeacherClassVO> page, TeacherClassVO teacherClass);

}
