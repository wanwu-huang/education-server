package org.mentpeak.test.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.mentpeak.core.mybatisplus.base.BaseService;
import org.mentpeak.test.dto.BindTeacherDTO;
import org.mentpeak.test.entity.ReportTeacherClass;
import org.mentpeak.test.entity.mongo.ClassReportData;
import org.mentpeak.test.entity.mongo.PersonalReport;
import org.mentpeak.test.vo.*;

import java.util.List;

/**
 * 老师班级报告表 服务类
 *
 * @author lxp
 * @since 2022-07-12
 */
public interface IReportTeacherClassService extends BaseService<ReportTeacherClass> {

    /**
     * 自定义分页
     *
     * @param page
     * @param reportTeacherClass
     * @return
     */
    IPage<ReportTeacherClassVO> selectReportTeacherClassPage(IPage<ReportTeacherClassVO> page, ReportTeacherClassVO reportTeacherClass);

    ClassReportVO addClassReport(Long taskId, Long gradeId, Long classId, String tenantCode);

    ClassReportVO getClassReport(Long taskId, Long gradeId, Long classId);

    TestModuleVO getTestPelple(Long taskId, Long gradeId, Long classId, ClassReportData reportData, ClassReportVO vo, String tenantCode);

    TestModuleTwoVO getMentalHealthy(Long taskId, Long gradeId, Long classId, ClassReportData reportData, String tenantCode);

    PersonalReport.TestOverview getTestScore(Long taskId, Long gradeId, Long classId, ClassReportData reportData, String tenantCode);

    List<DimensionReportVO> getStudyStatus(Long taskId, Long gradeId, Long classId, ClassReportData classReportData, String tenantCode);

    IPage<ReportClassListVO> getReportList(IPage<ReportClassListVO> page, Long taskId, Long gradeId, Long classId);

    boolean bindTeacher(BindTeacherDTO dto);

    List<BindTeacherVO> bindTeacherList(Long taskId, Long gradeId, Long classId);

    boolean removeBindTeacher(Long userId, Long taskId, Long gradeId, Long classId);
}
