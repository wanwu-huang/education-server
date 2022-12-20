package org.mentpeak.test.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Param;
import org.mentpeak.test.entity.ReportTeacherGrade;
import org.mentpeak.test.vo.BindTeacherVO;
import org.mentpeak.test.vo.ReportTeacherGradeVO;

import java.util.List;

/**
 * 老师年级报告表 Mapper 接口
 *
 * @author lxp
 * @since 2022-07-12
 */
public interface ReportTeacherGradeMapper extends BaseMapper<ReportTeacherGrade> {

    /**
     * 自定义分页
     *
     * @param page
     * @param reportTeacherGrade
     * @return
     */
    List<ReportTeacherGradeVO> selectReportTeacherGradePage(IPage page, ReportTeacherGradeVO reportTeacherGrade);

    List<BindTeacherVO> getBindTeacherList(@Param("taskId") Long taskId, @Param("gradeId") Long gradeId);

}
