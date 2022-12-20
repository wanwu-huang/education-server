package org.mentpeak.test.strategy;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mentpeak.common.constant.CommonConstant;
import org.mentpeak.core.redis.core.RedisService;
import org.mentpeak.core.tool.utils.Func;
import org.mentpeak.core.tool.utils.ObjectUtil;
import org.mentpeak.test.base.BaseOption;
import org.mentpeak.test.entity.TestOption;
import org.mentpeak.test.entity.TestQuestion;
import org.mentpeak.test.mapper.TestOptionMapper;
import org.mentpeak.test.mapper.TestQuestionMapper;
import org.mentpeak.test.vo.QuestionVO;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author hzl
 * @data 2022年07月14日15:15
 * 公共实现类
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class CommonService {

    private final TestQuestionMapper questionMapper;

    private final TestOptionMapper optionMapper;

    private final RedisService redisService;

    private final RedisTemplate redisTemplate;

    /**
     * 根据类型刷入题库
     *
     * @param type
     * @return boolean
     * @author hzl
     * @date 2022/7/14 17:35
     */
    public boolean setDataToRedis(Integer type) {
        String key = CommonConstant.QUESTIONNAIRE + type;
        Boolean hasKey = redisService.hasKey(key);
        if (!hasKey) {
            List<TestQuestion> questionList = questionMapper.selectList(Wrappers.<TestQuestion>lambdaQuery()
                    .eq(TestQuestion::getModuleId, type)
                    .eq(TestQuestion::getIsDeleted, 0)
                    .orderBy(true, true, TestQuestion::getSort));
            List<QuestionVO> questionVOList = new ArrayList<>();
            log.info("试题信息：{}", JSON.toJSON(questionList));
            questionList.stream().forEach(testQuestion -> {
                QuestionVO vo = new QuestionVO();
                vo.setModuleId(testQuestion.getModuleId());
                vo.setQId(Func.toStr(testQuestion.getId()));
                vo.setQTitle(testQuestion.getTitle());
                vo.setQType(Func.toInt(testQuestion.getQuestionType()));
                vo.setSort(testQuestion.getSort());
                // 题支
                List<BaseOption> optionList = optionMapper.selectList(Wrappers.<TestOption>lambdaQuery()
                        .eq(TestOption::getQuestionId, testQuestion.getId())
                        .eq(TestOption::getIsDeleted, 0))
                        .stream().map(testOption -> {
                            BaseOption option = new BaseOption();
                            option.setOId(Func.toStr(testOption.getId()));
                            option.setOTitle(testOption.getTitle());
                            return option;
                        }).collect(Collectors.toList());
                vo.setOptionList(optionList);
                questionVOList.add(vo);
            });
            redisTemplate.opsForList().rightPushAll(key, questionVOList);
            // 设置过期时间1~3天随机
            redisTemplate.expire(key, new Integer((int) (Math.random() * 3 + 1)), TimeUnit.DAYS);
        }
        return true;
    }

    /**
     * @return boolean
     * @author hzl
     * @date 2022/7/14 17:36
     */
    public boolean setAllToRedis() {
        List<TestQuestion> questionList = questionMapper.selectList(Wrappers.<TestQuestion>lambdaQuery()
                .eq(TestQuestion::getIsDeleted, 0)
                .orderBy(true, true, TestQuestion::getModuleId)
                .orderBy(true, true, TestQuestion::getSort));
        // 根据模块进行分组
        Map<Long, List<TestQuestion>> map = questionList.stream().collect(Collectors.groupingBy(TestQuestion::getModuleId));
        for (Map.Entry<Long, List<TestQuestion>> entry : map.entrySet()) {
            String key = CommonConstant.QUESTIONNAIRE + entry.getKey();
            Boolean hasKey = redisService.hasKey(key);
            if (!hasKey) {
                log.info("试题信息：{}", JSON.toJSON(entry.getValue()));
                List<QuestionVO> questionVOList = new ArrayList<>();
                entry.getValue().stream().forEach(testQuestion -> {
                    QuestionVO vo = new QuestionVO();
                    vo.setModuleId(testQuestion.getModuleId());
                    vo.setQId(Func.toStr(testQuestion.getId()));
                    vo.setQTitle(testQuestion.getTitle());
                    vo.setQType(Func.toInt(testQuestion.getQuestionType()));
                    vo.setSort(testQuestion.getSort());
                    // 题支
                    List<BaseOption> optionList = optionMapper.selectList(Wrappers.<TestOption>lambdaQuery()
                            .eq(TestOption::getQuestionId, testQuestion.getId())
                            .eq(TestOption::getIsDeleted, 0))
                            .stream().map(testOption -> {
                                BaseOption option = new BaseOption();
                                option.setOId(Func.toStr(testOption.getId()));
                                option.setOTitle(testOption.getTitle());
                                return option;
                            }).collect(Collectors.toList());
                    vo.setOptionList(optionList);
                    questionVOList.add(vo);
                });
//                redisService.lSet(key, questionVOList)    ;
                redisTemplate.opsForList().rightPushAll(key, questionVOList);
                // 设置过期时间1~3天随机
//                redisTemplate.expire(key, new Integer((int) (Math.random() * 3 + 1)), TimeUnit.DAYS);
            }
        }
        return true;
    }

    public QuestionVO getQuestion(String key, Long moduleId, int index, int moduleCount, Long size) {
        Object o = redisService.lGetIndex(key, Func.toLong(index));
        QuestionVO convert = null;
        if (ObjectUtil.isNotEmpty(o)) {
            convert = Func.convert(o, QuestionVO.class);
        } else {
            // 重新刷入题库
            setDataToRedis(moduleId.intValue());
            o = redisService.lGetIndex(key, Func.toLong(index));
            convert = Func.convert(o, QuestionVO.class);
        }
        convert.setModuleIndex(index);
        convert.setModuleCount(moduleCount);
        // 判断是否还有下一题
        if ((index + 1) >= size) {
            // 没有下一题
            convert.setIsNextId(0);
        }
        return convert;
    }
}
