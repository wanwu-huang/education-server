package org.mentpeak.test.mapper;

import com.alicp.jetcache.anno.Cached;
import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.mentpeak.test.entity.BindTeacher;
import org.mentpeak.test.entity.TestTaskDepartment;
import org.mentpeak.test.vo.ReportClassListVO;
import org.mentpeak.test.vo.ReportListVO;
import org.mentpeak.test.vo.TestTaskDepartmentVO;

import java.util.List;

/**
 * 测评任务部门关联表 Mapper 接口
 *
 * @author lxp
 * @since 2022-07-12
 */
public interface TestTaskDepartmentMapper extends BaseMapper<TestTaskDepartment> {

    /**
     * 自定义分页
     *
     * @param page
     * @param testTaskDepartment
     * @return
     */
    List<TestTaskDepartmentVO> selectTestTaskDepartmentPage(IPage page, TestTaskDepartmentVO testTaskDepartment);

    Page<ReportListVO> getGradeList(IPage<ReportListVO> page, @Param("taskId") Long taskId, @Param("gradeId") Long gradeId);

    List<ReportListVO> getGradeList2(@Param("taskId") Long taskId, @Param("gradeId") Long gradeId);

    Page<ReportClassListVO> getClassList(IPage<ReportClassListVO> page, @Param("taskId") Long taskId, @Param("gradeId") Long gradeId, @Param("classId") Long classId);

    List<ReportClassListVO> getClassList2(@Param("taskId") Long taskId, @Param("gradeId") Long gradeId, @Param("classId") Long classId);

    BindTeacher getUserByPhone(@Param("phone") String phone);

    /**
     * 根据任务ID获取部门ID
     * @param taskId 任务ID
     * @return
     */
    @Cached(name = "education:departmentId:",key = "#taskId",expire = 60)
    @InterceptorIgnore(tenantLine = "true")
    List<Integer> getDepartmentIdListByTaskId(@Param("taskId") Long taskId);

}
