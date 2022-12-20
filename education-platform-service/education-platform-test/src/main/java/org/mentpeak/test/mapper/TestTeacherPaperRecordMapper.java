package org.mentpeak.test.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.mentpeak.test.entity.TestTeacherPaperRecord;
import org.mentpeak.test.vo.TestTeacherPaperRecordVO;

import java.util.List;

/**
 * 教师评定问卷测试记录表 Mapper 接口
 *
 * @author lxp
 * @since 2022-08-15
 */
public interface TestTeacherPaperRecordMapper extends BaseMapper<TestTeacherPaperRecord> {

	/**
	 * 自定义分页
	 *
	 * @param page
	 * @param testTeacherPaperRecord
	 * @return
	 */
	List<TestTeacherPaperRecordVO> selectTestTeacherPaperRecordPage(IPage page, TestTeacherPaperRecordVO testTeacherPaperRecord);

}
