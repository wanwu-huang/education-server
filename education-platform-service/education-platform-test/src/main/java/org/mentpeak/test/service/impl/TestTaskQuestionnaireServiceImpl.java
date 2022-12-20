package org.mentpeak.test.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.mentpeak.core.auth.utils.SecureUtil;
import org.mentpeak.test.entity.TestPaper;
import org.mentpeak.test.entity.TestTaskQuestionnaire;
import org.mentpeak.test.mapper.TestPaperMapper;
import org.mentpeak.test.mapper.TestTaskQuestionnaireMapper;
import org.mentpeak.test.service.ITestTaskQuestionnaireService;
import org.mentpeak.test.vo.TaskQuestionnaireVO;
import org.mentpeak.test.vo.TestTaskQuestionnaireVO;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 测评任务问卷关联表 服务实现类
 *
 * @author lxp
 * @since 2022-07-12
 */
@Service
@RequiredArgsConstructor
public class TestTaskQuestionnaireServiceImpl extends ServiceImpl<TestTaskQuestionnaireMapper, TestTaskQuestionnaire> implements ITestTaskQuestionnaireService {

    private final TestPaperMapper testPaperMapper;

    @Override
    public IPage<TestTaskQuestionnaireVO> selectTestTaskQuestionnairePage(IPage<TestTaskQuestionnaireVO> page, TestTaskQuestionnaireVO testTaskQuestionnaire) {
        return page.setRecords(baseMapper.selectTestTaskQuestionnairePage(page, testTaskQuestionnaire));
    }

    @Override
    public List<TaskQuestionnaireVO> getListById(Long id) {
        Long userId = SecureUtil.getUserId();
        List<TaskQuestionnaireVO> listById = baseMapper.getListById(id);
        listById.stream().forEach(taskQuestionnaireVO -> {
            // 根据用户id,任务id,问卷id,完成标识查找试卷表，是否该用户已完成该问卷
            taskQuestionnaireVO.setIsFinish(getCount(userId, taskQuestionnaireVO.getTaskId(), taskQuestionnaireVO.getQuestionnaireId()));
        });
        return listById;
    }

    //    @Cached(name = "testPaper", expire = 360, cacheType = CacheType.BOTH, key = "#taskId+':'+#questionnaireId+':'+#userId")
//    作用于切面，方法内部失效
    public int getCount(Long userId, Long taskId, Long questionnaireId) {
        Long count = testPaperMapper.selectCount(Wrappers.<TestPaper>lambdaQuery()
                .eq(TestPaper::getUserId, userId)
                .eq(TestPaper::getQuestionnaireId, questionnaireId)
                .eq(TestPaper::getTaskId, taskId)
                .eq(TestPaper::getIsFinish, 1));
        if (count > 0) {
            return 1;
        }
        return 0;
    }

}
