package org.mentpeak.test.mapper;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Param;
import org.mentpeak.test.entity.TestPaper;
import org.mentpeak.test.vo.InstructionVO;
import org.mentpeak.test.vo.TestPaperVO;

import java.util.List;

/**
 * 用户试卷信息表 Mapper 接口
 *
 * @author lxp
 * @since 2022-07-12
 */
public interface TestPaperMapper extends BaseMapper<TestPaper> {

    /**
     * 自定义分页
     *
     * @param page
     * @param testPaper
     * @return
     */
    List<TestPaperVO> selectTestPaperPage(IPage page, TestPaperVO testPaper);

    InstructionVO getInstruction(@Param("moduleId") Long moduleId);

    /**
     * 获取学生总人数
     *
     * @param classId
     * @return java.lang.Long
     * @author hzl
     * @date 2022/8/11 10:37
     */
    @InterceptorIgnore(tenantLine = "true")
    Long getTotalPeople(@Param("gradeId") Long gradeId, @Param("classId") Long classId,@Param("tenantCode") String tenantCode);

    /**
     * 获取学生测试人数
     *
     * @param taskId
     * @param classId
     * @return java.lang.Long
     * @author hzl
     * @date 2022/8/11 10:37
     */
    @InterceptorIgnore(tenantLine = "true")
    List<Long> getTestPeople(@Param("taskId") Long taskId, @Param("gradeId") Long gradeId, @Param("classId") Long classId,@Param("tenantCode") String tenantCode);

    /**
     * 获取家长测试人数
     *
     * @param classId
     * @return java.lang.Long
     * @author hzl
     * @date 2022/8/11 10:34
     */
    @InterceptorIgnore(tenantLine = "true")
    List<Integer> getTestParent(@Param("gradeId") Long gradeId,@Param("classId") Long classId);

    /**
     * 获取家长总人数
     *
     * @param classId
     * @return java.lang.Integer
     * @author hzl
     * @date 2022/8/11 10:38
     */
    @InterceptorIgnore(tenantLine = "true")
    Integer getParentPeople(@Param("gradeId") Long gradeId,@Param("classId") Long classId);

    /**
     * 获取班级名称
     *
     * @param classId
     * @return java.lang.String
     * @author hzl
     * @date 2022/8/11 13:55
     */
    String getClassName(@Param("classId") Long classId);

    /**
     * 根据任务、班级id,获取该班级学生最新试卷id
     *
     * @param classId
     * @return java.util.List<java.lang.Long>
     * @author hzl
     * @date 2022/8/15 11:34
     */
    @InterceptorIgnore(tenantLine = "true")
    List<String> getPaperIdByCId(@Param("taskId") Long taskId, @Param("gradeId") Long gradeId,@Param("classId") Long classId,@Param("tenantCode") String tenantCode);
    @InterceptorIgnore(tenantLine = "true")
    List<String> getAllClassPaperId(@Param("taskId") Long taskId, @Param("gradeId") Long gradeId,@Param("classId") Long classId,@Param("tenantCode") String tenantCode);
    @InterceptorIgnore(tenantLine = "true")
    List<String> getAllGradePaperId(@Param("taskId") Long taskId, @Param("gradeId") Long gradeId,@Param("classId") Long classId,@Param("tenantCode") String tenantCode);
    /**
     * 根据班级id,获取该年级所有的班级id
     *
     * @param classId
     * @return java.util.List<java.lang.Long>
     * @author hzl
     * @date 2022/8/15 11:34
     */
    List<Long> getAllClassId(@Param("classId") Long classId);

    /**
     * 根据年级id,获取该年级所有的班级id
     *
     * @param gradeId
     * @return java.util.List<java.lang.Long>
     * @author hzl
     * @date 2022/8/15 11:34
     */
    List<Long> getClassByGradeId(@Param("gradeId") Long gradeId);

}
