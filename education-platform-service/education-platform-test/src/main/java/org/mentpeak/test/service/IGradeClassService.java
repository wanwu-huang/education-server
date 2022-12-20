package org.mentpeak.test.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.mentpeak.test.entity.GradeClass;
import org.mentpeak.test.vo.GradeClassVO;

/**
 * 年级-班级对应关联表 服务类
 *
 * @author lxp
 * @since 2022-08-12
 */
public interface IGradeClassService extends IService<GradeClass> {

	/**
	 * 自定义分页
	 *
	 * @param page
	 * @param gradeClass
	 * @return
	 */
	IPage<GradeClassVO> selectGradeClassPage(IPage<GradeClassVO> page, GradeClassVO gradeClass);

}
