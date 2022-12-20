package org.mentpeak.test.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.mentpeak.core.mybatisplus.base.BaseService;
import org.mentpeak.test.dto.NextQuestionDTO;
import org.mentpeak.test.dto.QuestionDTO;
import org.mentpeak.test.entity.TestPaper;
import org.mentpeak.test.vo.*;
import org.springframework.transaction.annotation.Transactional;

/**
 * 用户试卷信息表 服务类
 *
 * @author lxp
 * @since 2022-07-12
 */
public interface ITestPaperService extends BaseService<TestPaper> {

    /**
     * 自定义分页
     *
     * @param page
     * @param testPaper
     * @return
     */
    IPage<TestPaperVO> selectTestPaperPage(IPage<TestPaperVO> page, TestPaperVO testPaper);

    InstructionVO getReminder(Long moduleId);

    /**
     * 开始答题
     *
     * @param generateVO
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    QuestionVO generatePaper(GenerateVO generateVO);

    /**
     * 获取下一题
     *
     * @param nextQuestionDTO
     * @return org.mentpeak.test.vo.QuestionVO
     * @author hzl
     * @date 2022/7/15 11:40
     */
    QuestionVO getNextQuestion(NextQuestionDTO nextQuestionDTO);

    @Transactional(rollbackFor = Exception.class)
    AnswerVo saveResult(QuestionDTO questionDTO);

}
