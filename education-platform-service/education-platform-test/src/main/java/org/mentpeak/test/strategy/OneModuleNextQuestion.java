package org.mentpeak.test.strategy;

import lombok.RequiredArgsConstructor;
import org.mentpeak.common.constant.CommonConstant;
import org.mentpeak.core.redis.core.RedisService;
import org.mentpeak.core.tool.utils.Func;
import org.mentpeak.core.tool.utils.ObjectUtil;
import org.mentpeak.test.vo.QuestionVO;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author hzl
 * @date 2022/7/14
 * @description
 */
@Service
@RequiredArgsConstructor
public class OneModuleNextQuestion implements ModuleStrategy {

    private final CommonService commonService;

    private final ModuleStrategyFactory moduleStrategyFactory;

    private final RedisTemplate redisTemplate;

    @Override
    public Integer type() {
        return 1;
    }

    @Override
    public boolean setDateToRedis(Integer type) {
        return commonService.setDataToRedis(type);
    }

    @Override
    public QuestionVO getNextQuestion(Long moduleId, int index, int moduleCount) {
        String key = CommonConstant.QUESTIONNAIRE + moduleId;
        Long size = redisTemplate.opsForList().size(key);
        if (size == 0) {
            // 该模块没有题目，刷入redis
            setDateToRedis(Func.toInt(moduleId));
            size = redisTemplate.opsForList().size(key);
        }
        // 判断是否超过下标
        if (index >= size) {
            // 超过下标,查找下一量表  第九模块的第一题，次数为1
            ModuleStrategy strategy = moduleStrategyFactory.getStrategy(9);
            return strategy.getNextQuestion(9L, 0, 1);
        }
        return commonService.getQuestion(key, moduleId, index, moduleCount, Func.toLong(size));
    }
}
