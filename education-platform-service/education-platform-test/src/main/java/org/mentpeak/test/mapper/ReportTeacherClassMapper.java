package org.mentpeak.test.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Param;
import org.mentpeak.test.entity.ReportTeacherClass;
import org.mentpeak.test.vo.BindTeacherVO;
import org.mentpeak.test.vo.ReportTeacherClassVO;

import java.util.List;

/**
 * 老师班级报告表 Mapper 接口
 *
 * @author lxp
 * @since 2022-07-12
 */
public interface ReportTeacherClassMapper extends BaseMapper<ReportTeacherClass> {

    /**
     * 自定义分页
     *
     * @param page
     * @param reportTeacherClass
     * @return
     */
    List<ReportTeacherClassVO> selectReportTeacherClassPage(IPage page, ReportTeacherClassVO reportTeacherClass);

    List<BindTeacherVO> getBindTeacherList(@Param("taskId") Long taskId, @Param("gradeId") Long gradeId, @Param("classId") Long classId);

    /**
     * 查询老师能看到的学生信息ID
     * @param taskId
     * @param teacherId
     * @return
     */
    List<Long> getUserList(@Param("taskId") Long taskId, @Param("teacherId") Long teacherId);

}
