package org.mentpeak.test.strategy;

import lombok.RequiredArgsConstructor;
import org.mentpeak.common.constant.CommonConstant;
import org.mentpeak.core.redis.core.RedisService;
import org.mentpeak.core.tool.utils.Func;
import org.mentpeak.test.vo.QuestionVO;
import org.springframework.stereotype.Service;

/**
 * @author hzl
 * @date 2022/7/14
 * @description
 */
@Service
@RequiredArgsConstructor
public class FiveModuleNextQuestion implements ModuleStrategy {

    private final CommonService commonService;

    private final RedisService redisService;

    private final ModuleStrategyFactory moduleStrategyFactory;

    @Override
    public Integer type() {
        return 5;
    }

    @Override
    public boolean setDateToRedis(Integer type) {
        return commonService.setDataToRedis(type);
    }

    @Override
    public QuestionVO getNextQuestion(Long moduleId, int index, int moduleCount) {
        String key = CommonConstant.QUESTIONNAIRE + moduleId;
        Long size = redisService.lGetListSize(key);
        if (size == 0) {
            // 该模块没有题目，刷入redis
            setDateToRedis(Func.toInt(moduleId));
            size = redisService.lGetListSize(key);
        }
        // 判断是否超过下标
        if (index >= size) {
            // 超过下标,查找下一量表  第六模块
            ModuleStrategy strategy = moduleStrategyFactory.getStrategy(6);
            return strategy.getNextQuestion(6L, 0, 1);
        }
        return commonService.getQuestion(key, moduleId, index, moduleCount, size);
    }
}
