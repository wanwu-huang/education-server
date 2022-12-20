package org.mentpeak.test.mapper;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.mentpeak.test.entity.FollowWarn;
import org.mentpeak.test.entity.TestTaskUser;
import org.mentpeak.test.vo.FollowVO;
import org.mentpeak.test.vo.ReportUserVO;
import org.mentpeak.test.vo.TestTaskUserVO;
import org.mentpeak.test.vo.UserInfoVO;
import org.mentpeak.user.entity.UserExt;

import java.util.List;

/**
 * 测评任务用户关联表 Mapper 接口
 *
 * @author lxp
 * @since 2022-07-12
 */
public interface TestTaskUserMapper extends BaseMapper<TestTaskUser> {

    /**
     * 自定义分页
     *
     * @param page
     * @param testTaskUser
     * @return
     */
    List<TestTaskUserVO> selectTestTaskUserPage(IPage page, TestTaskUserVO testTaskUser);

    Page<FollowVO> getFollewList(IPage<FollowVO> page, @Param("followId") Long followId, @Param("taskId") Long taskId);

    @InterceptorIgnore(tenantLine = "true")
    List<UserInfoVO> getInvalidPeople(@Param("taskId") Long taskId, @Param("gradeId") Long gradeId, @Param("classId") Long classId,@Param("tenantCode") String tenantCode);

    @InterceptorIgnore(tenantLine = "true")
    List<FollowWarn> getFollowWarn(@Param("taskId") Long taskId, @Param("gradeId") Long gradeId, @Param("classId") Long classId);

    @InterceptorIgnore(tenantLine = "true")
    List<Long> getEffPeople(@Param("taskId") Long taskId, @Param("gradeId") Long gradeId, @Param("classId") Long classId);

    @InterceptorIgnore(tenantLine = "true")
    List<Long> getAllPeople(@Param("taskId") Long taskId, @Param("gradeId") Long gradeId, @Param("classId") Long classId);

    /**
     * 个人报告列表
     *
     * @param page
     * @param gradeId
     * @param realName
     * @param classId
     * @return
     */
    Page<ReportUserVO> reportList(IPage<ReportUserVO> page, @Param("gradeId") Long gradeId,
                                  @Param("realName") String realName, @Param("classId") Long classId, @Param("taskId") Long taskId, @Param("userIdList") List<Long> userIdList);


    @InterceptorIgnore(tenantLine = "true")
    TestTaskUser getTaskUser(@Param("userId") Long userId, @Param("taskId") Long taskId);

    @InterceptorIgnore(tenantLine = "true")
    void updateTaskUser(@Param("id") Long id, @Param("riskIndex") Integer riskIndex);

    @InterceptorIgnore(tenantLine = "true")
    UserExt getUserExtById(@Param("userId") Long userId);
}
