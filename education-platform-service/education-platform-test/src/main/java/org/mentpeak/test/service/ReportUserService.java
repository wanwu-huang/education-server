package org.mentpeak.test.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import java.util.List;
import org.mentpeak.test.dto.ReportUserDTO;
import org.mentpeak.test.entity.mongo.PersonalReport;
import org.mentpeak.test.vo.ReportUserVO;

/**
 * 个人报告 服务
 * @author demain_lee
 * @since 2022-08-09
 */
public interface ReportUserService {


    /**
     *  报告信息
     * @param taskId 任务ID
     * @param userId 用户ID
     * @return 报告信息
     */
    PersonalReport reportInfo(Long taskId,Long userId);

    /**
     *  生成个人报告
     * @param paperId 试卷ID
     * @param userId  用户ID
     * @return
     */
    boolean generateReport(Long paperId,Long userId);

    /**
     * 报告列表
     * @param page
     * @param reportUserDTO
     * @return
     */
    Page<ReportUserVO> reportList(IPage<ReportUserVO> page, ReportUserDTO reportUserDTO);

    /**
     * 修改个人报告教师评定数据
     * @param userIdList 学生ID
     * @return
     */
    boolean updatePersonalReportsTeacherRating(List<Long> userIdList);

    /**
     * 修改个人报告家长他评数据
     * @param userId 学生ID
     * @return
     */
    boolean updatePersonalReportsParentRating(Long userId);
}
