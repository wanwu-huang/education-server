package org.mentpeak.test.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.mentpeak.core.auth.PlatformUser;
import org.mentpeak.core.auth.utils.SecureUtil;
import org.mentpeak.core.mybatisplus.base.BaseServiceImpl;
import org.mentpeak.test.dto.TaskSearchDTO;
import org.mentpeak.test.entity.TestTaskUser;
import org.mentpeak.test.mapper.MenuDataMapper;
import org.mentpeak.test.mapper.TestTaskMapper;
import org.mentpeak.test.mapper.TestTaskUserMapper;
import org.mentpeak.test.service.ITestTaskUserService;
import org.mentpeak.test.vo.FollowVO;
import org.mentpeak.test.vo.TaskWarnVO;
import org.mentpeak.test.vo.TestRecordVO;
import org.mentpeak.test.vo.TestTaskUserVO;
import org.mentpeak.user.entity.MenuData;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 测评任务用户关联表 服务实现类
 *
 * @author lxp
 * @since 2022-07-12
 */
@Service
@RequiredArgsConstructor
public class TestTaskUserServiceImpl extends BaseServiceImpl<TestTaskUserMapper, TestTaskUser> implements ITestTaskUserService {

    private final TestTaskMapper testTaskMapper;

    private final MenuDataMapper menuDataMapper;

    @Override
    public IPage<TestTaskUserVO> selectTestTaskUserPage(IPage<TestTaskUserVO> page, TestTaskUserVO testTaskUser) {
        return page.setRecords(baseMapper.selectTestTaskUserPage(page, testTaskUser));
    }

    @Override
    public Page<TaskWarnVO> warnList(IPage<TestRecordVO> page, TaskSearchDTO taskSearchDTO) {
        // 普通管理员需要判断该任务是否显示
        PlatformUser user = SecureUtil.getUser();
        Page<TaskWarnVO> taskInfoVoPage = null;
        List<Long> collect = null;
        if ("2".equals(user.getRoleId())) {
            // 查找开通权限的任务id
            List<MenuData> menuData = menuDataMapper.selectList(Wrappers.<MenuData>lambdaQuery()
                    .eq(MenuData::getUserId, user.getUserId())
                    .eq(MenuData::getMenuId, 12)
                    .eq(MenuData::getIsDeleted, 0));
            collect = menuData.stream().map(MenuData::getDataId).collect(Collectors.toList());
        }
        taskInfoVoPage = testTaskMapper.getWarnList(page, taskSearchDTO.getTaskName(), taskSearchDTO.getTaskCreateTime(), taskSearchDTO.getBeginTime(), taskSearchDTO.getEndTime(), taskSearchDTO.getTaskStatus(), collect,SecureUtil.getTenantCode());
        List<TaskWarnVO> taskInfoVOList = taskInfoVoPage.getRecords();
        taskInfoVOList.forEach(task -> {
            Integer totalTestCount = testTaskMapper.totalTestCount(task.getId());
            task.setTestPeopleCount(totalTestCount);
            task.setTaskStatusName(task.getTaskStatus().equals(0) ? "未完成" : "已完成");
            // 预警人数
            Long count = baseMapper.selectCount(Wrappers.<TestTaskUser>lambdaQuery()
                    .eq(TestTaskUser::getTestTaskId, task.getId())
                    .ne(TestTaskUser::getIsWarn, 0));
            task.setWarnPeopleCount(count.intValue());
        });
        return taskInfoVoPage;
    }

    @Override
    public IPage<FollowVO> followList(IPage<FollowVO> page, Long followId, Long taskId) {
        return baseMapper.getFollewList(page, followId, taskId);
    }

}
