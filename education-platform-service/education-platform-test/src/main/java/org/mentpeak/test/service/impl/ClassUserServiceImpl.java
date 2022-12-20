package org.mentpeak.test.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mentpeak.core.auth.utils.SecureUtil;
import org.mentpeak.core.log.exception.PlatformApiException;
import org.mentpeak.core.tool.utils.Func;
import org.mentpeak.test.dto.GradeDetailUserDTO;
import org.mentpeak.test.entity.ClassUser;
import org.mentpeak.test.entity.TestTaskDepartment;
import org.mentpeak.test.entity.TestTaskUser;
import org.mentpeak.test.mapper.*;
import org.mentpeak.test.service.IClassUserService;
import org.mentpeak.test.service.IReportTeacherClassService;
import org.mentpeak.test.vo.ClassUserVO;
import org.mentpeak.test.vo.GradeDetailUserVO;
import org.mentpeak.test.vo.GradeUserVO;
import org.mentpeak.user.entity.UserExt;
import org.mentpeak.user.feign.IUserClient;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * 班级用户表 服务实现类
 *
 * @author lxp
 * @since 2022-07-12
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ClassUserServiceImpl extends ServiceImpl<ClassUserMapper, ClassUser> implements IClassUserService {


    private final IUserClient userClient;

    private final GradeClassMapper gradeClassMapper;

    private final TeacherClassMapper teacherClassMapper;

    private final IReportTeacherClassService reportTeacherClassService;

    private final TestTaskDepartmentMapper testTaskDepartmentMapper;

    private final TestTaskUserMapper taskUserMapper;

    @Override
    public IPage<ClassUserVO> selectClassUserPage(IPage<ClassUserVO> page, ClassUserVO classUser) {
        return page.setRecords(baseMapper.selectClassUserPage(page, classUser));
    }

    @Override
    public Page<GradeUserVO> userManagerList(IPage<GradeUserVO> page, Long gradeId) {
        return baseMapper.userManagerList(page, gradeId);
    }

    @Override
    public Page<GradeDetailUserVO> userManagerDetailList(IPage<GradeDetailUserVO> page, GradeDetailUserDTO gradeDetailUserDTO) {
        Page<GradeDetailUserVO> gradeDetailUserVoPage = baseMapper.userManagerDetailList(page, gradeDetailUserDTO.getGradeId(), gradeDetailUserDTO.getRealName(),
                gradeDetailUserDTO.getAccount(), gradeDetailUserDTO.getClassId(), gradeDetailUserDTO.getSex());
        List<GradeDetailUserVO> gradeDetailUserVOList = gradeDetailUserVoPage.getRecords();
        gradeDetailUserVOList.forEach(gradeDetailUserVO -> {
            if (Func.isNotEmpty(gradeDetailUserVO.getSex())) {
                gradeDetailUserVO.setSexName(gradeDetailUserVO.getSex() == 0 ? "男" : "女");
            }
        });
        return gradeDetailUserVoPage;
    }

    @Override
    public Boolean deleteById(Long userId) {

        try {
            // 如果有任务记录不能删除
            List<TestTaskUser> testTaskUserList = taskUserMapper.selectList(
                    Wrappers.<TestTaskUser>lambdaQuery().eq(TestTaskUser::getUserId, userId).eq(
                            TestTaskUser::getCompletionStatus, 1));
            if (testTaskUserList.size() > 0) {
                throw new PlatformApiException("已有测评数据，不能删除该用户");
            }

            // 删除用户信息
            UserExt userExt = userClient.userExtInfoById(userId).getData();
            userClient.deleteUserInfoById(userId);
            String tenantCode = SecureUtil.getTenantCode();
            // 更新班级数据
            RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
            CompletableFuture.runAsync(() -> {
                RequestContextHolder.setRequestAttributes(requestAttributes);
                updateClassReportData(userExt, tenantCode);
            });
        } catch (Exception e) {
            e.printStackTrace();
            throw new PlatformApiException("删除用户失败");
        }
        return true;
    }

    void updateClassReportData(UserExt userExt, String tenantCode) {
        // 获取任务ID
        if (Func.isNotEmpty(userExt.getGrade())) {
            List<TestTaskDepartment> testTaskDepartments = testTaskDepartmentMapper.selectList(Wrappers.<TestTaskDepartment>lambdaQuery()
                    .eq(TestTaskDepartment::getTestDepartmentId, userExt.getGrade()));

            testTaskDepartments.forEach(testTaskDepartment -> {
                if (Func.isNotEmpty(userExt.getGrade()) && Func.isNotEmpty(userExt.getClassId())) {
                    reportTeacherClassService.addClassReport(testTaskDepartment.getTestTaskId(), Func.toLong(userExt.getGrade()), userExt.getClassId(), tenantCode);
                }
                log.info("更新班级报告数据");
            });
        }

    }

    @Override
    public Boolean deleteByGradeId(Long gradeId) {

        /**
         * 查看年级下的学生是否有数据
         * 有则不进行操作
         * 否则删除 删除年级班级  班级学生  班级老师 关系
         */
        Integer count = baseMapper.taskCountByGradeId(gradeId);
        if (count > 0) {
            throw new PlatformApiException("已有测评数据，不能删除该部门");
        }

        // 删除关联数据

        // 学生ID
        List<Long> userIdList = baseMapper.userIdListByGradeId(gradeId.toString());

        // 删除年级老师关联关系
        teacherClassMapper.deleteByGradeId(gradeId);

        // 年级班级设为空 同时 删除 学生 以及拓展表
        // 批量修改
        baseMapper.updateUserInfoByUserId(userIdList);
        baseMapper.updateClassGradeInfoByUserId(userIdList);

        return true;
    }
}
