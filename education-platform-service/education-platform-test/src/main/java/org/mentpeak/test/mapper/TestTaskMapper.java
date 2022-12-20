package org.mentpeak.test.mapper;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.lettuce.core.dynamic.annotation.Param;
import org.mentpeak.test.entity.TestTask;
import org.mentpeak.test.vo.*;

import java.util.List;

/**
 * 测评任务表 Mapper 接口
 *
 * @author lxp
 * @since 2022-07-12
 */
public interface TestTaskMapper extends BaseMapper<TestTask> {

    /**
     * 自定义分页
     *
     * @param page
     * @param testTask
     * @return
     */
    List<TestTaskVO> selectTestTaskPage(IPage page, TestTaskVO testTask);


    /**
     * 任务管理列表
     *
     * @param page page
     * @param taskName 任务名称
     * @param taskCreateTime 任务创建时间
     * @param beginTime 任务开始时间
     * @param endTime 任务结束时间
     * @param taskStatus 任务状态
     * @return info
     */
    Page<TaskInfoVO> getTestTaskList(IPage<TestRecordVO> page, @Param("taskName") String taskName,
                                     @Param("taskCreateTime") String taskCreateTime, @Param("beginTime") String beginTime,
                                     @Param("endTime") String endTime, @Param("taskStatus") Integer taskStatus,
                                     @Param("taskIdList") List<Long> taskIdList,@Param("tenantCode") String tenantCode);

    /**
     * 预警管理列表
     *
     * @param page page
     * @param taskName 任务名称
     * @param taskCreateTime 任务创建时间
     * @param beginTime 任务开始时间
     * @param endTime 任务结束时间
     * @param taskStatus 任务状态
     * @return info
     */

    Page<TaskWarnVO> getWarnList(IPage<TestRecordVO> page, @Param("taskName") String taskName,
                                 @Param("taskCreateTime") String taskCreateTime, @Param("beginTime") String beginTime,
                                 @Param("endTime") String endTime, @Param("taskStatus") Integer taskStatus,@Param("taskIdList") List<Long> taskIdList,
                                 @Param("tenantCode") String tenantCode);


    /**
     * 根据任务ID 查询每个任务下 总测试人数
     * @param taskId 任务ID
     * @return 人数
     */
    Integer totalTestCount(Long taskId);


    /**
     * 根据任务ID获取测评任务详情
     * @param page page
     * @param taskId 任务ID
     * @param name 姓名
     * @param account 学籍号
     * @param sex 性别
     * @param beginTime 测评时间
     * @param endTime 测评时间
     * @param gradeId 一级部门
     * @param classId 二级部门
     * @param completionStatus 完成情况
     * @return t
     */
    Page<TaskDetailVO> taskDetailListById(IPage<TestRecordVO> page,  @Param("taskId") Long taskId,
                                          @Param("name") String name, @Param("account") String account,
                                          @Param("sex") Integer sex, @Param("beginTime") String beginTime,
                                          @Param("endTime") String endTime, @Param("gradeId") Long gradeId,
                                          @Param("classId") Long classId, @Param("completionStatus") Integer completionStatus);

    /**
     *  根据任务ID 完成状态查询任务信息
     * @param taskId 任务ID
     * @param completionStatus 完成状态
     * @return 任务信息
     */
    List<TaskDetailVO> taskDetailListByIdAndCompletionStatus(@Param("taskId") Long taskId, @Param("completionStatus") Integer completionStatus);


    /**
     * 完成任务用户信息
     * @param page
     * @param taskId
     * @return
     */
    Page<TaskUserInfoVO> taskUserInfoList(IPage<TaskUserInfoVO> page, @Param("taskId") Long taskId);

    /**
     * 任务用户信息
     * @param taskId
     * @param userId
     * @return
     */
    List<TaskUserInfoVO> taskUserInfo(@Param("taskId") Long taskId,@Param("userId") Long userId);

    /**
     * 用户信息
     * @param userId
     * @return
     */
    TaskUserInfoVO userInfoById(@Param("userId") Long userId);

    @InterceptorIgnore(tenantLine = "true")
    TestTask getTaskById(@Param("id") Long id);
}
