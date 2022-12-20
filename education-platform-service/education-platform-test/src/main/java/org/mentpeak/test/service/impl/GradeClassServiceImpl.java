package org.mentpeak.test.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.mentpeak.test.entity.GradeClass;
import org.mentpeak.test.mapper.GradeClassMapper;
import org.mentpeak.test.service.IGradeClassService;
import org.mentpeak.test.vo.GradeClassVO;
import org.springframework.stereotype.Service;

/**
 * 年级-班级对应关联表 服务实现类
 *
 * @author lxp
 * @since 2022-08-12
 */
@Service
public class GradeClassServiceImpl extends ServiceImpl<GradeClassMapper, GradeClass> implements IGradeClassService {

	@Override
	public IPage<GradeClassVO> selectGradeClassPage(IPage<GradeClassVO> page, GradeClassVO gradeClass) {
		return page.setRecords(baseMapper.selectGradeClassPage(page, gradeClass));
	}

}
