package org.mentpeak.test.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.mentpeak.core.mybatisplus.base.BaseServiceImpl;
import org.mentpeak.test.entity.TeacherClass;
import org.mentpeak.test.mapper.TeacherClassMapper;
import org.mentpeak.test.service.ITeacherClassService;
import org.mentpeak.test.vo.TeacherClassVO;
import org.springframework.stereotype.Service;

/**
 * 班主任班级关联表 服务实现类
 *
 * @author lxp
 * @since 2022-07-12
 */
@Service
public class TeacherClassServiceImpl extends BaseServiceImpl<TeacherClassMapper, TeacherClass> implements ITeacherClassService {

	@Override
	public IPage<TeacherClassVO> selectTeacherClassPage(IPage<TeacherClassVO> page, TeacherClassVO teacherClass) {
		return page.setRecords(baseMapper.selectTeacherClassPage(page, teacherClass));
	}

}
