package org.mentpeak.test.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.mentpeak.core.mybatisplus.base.BaseService;
import org.mentpeak.test.dto.BindTeacherDTO;
import org.mentpeak.test.entity.ReportTeacherGrade;
import org.mentpeak.test.vo.BindTeacherVO;
import org.mentpeak.test.vo.GradeReportVO;
import org.mentpeak.test.vo.ReportListVO;
import org.mentpeak.test.vo.ReportTeacherGradeVO;

import java.util.List;

/**
 * 老师年级报告表 服务类
 *
 * @author lxp
 * @since 2022-07-12
 */
public interface IReportTeacherGradeService extends BaseService<ReportTeacherGrade> {

    /**
     * 自定义分页
     *
     * @param page
     * @param reportTeacherGrade
     * @return
     */
    IPage<ReportTeacherGradeVO> selectReportTeacherGradePage(IPage<ReportTeacherGradeVO> page, ReportTeacherGradeVO reportTeacherGrade);

    GradeReportVO addGradeReport(Long taskId, Long gradeId, String tenantCode);

    GradeReportVO getGradeReport(Long taskId, Long gradeId);

    IPage<ReportListVO> getReportList(IPage<ReportListVO> page, Long id, Long taskId);

    boolean bindTeacher(BindTeacherDTO dto);

    List<BindTeacherVO> bindTeacherList(Long taskId, Long gradeId);

    boolean removeBindTeacher(Long userId, Long taskId, Long gradeId);
}
