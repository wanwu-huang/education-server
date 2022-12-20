package org.mentpeak.test.strategy;

import lombok.RequiredArgsConstructor;
import org.mentpeak.common.constant.CommonConstant;
import org.mentpeak.core.redis.core.RedisService;
import org.mentpeak.core.tool.utils.Func;
import org.mentpeak.core.tool.utils.ObjectUtil;
import org.mentpeak.test.vo.QuestionVO;
import org.springframework.stereotype.Service;

/**
 * @author hzl
 * @date 2022/7/14
 * @description
 */
@Service
@RequiredArgsConstructor
public class NineModuleNextQuestion implements ModuleStrategy {

    private final CommonService commonService;

    private final RedisService redisService;

    private final ModuleStrategyFactory moduleStrategyFactory;

    @Override
    public Integer type() {
        return 9;
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
            // 超过下标,查找下一量表  第5模块
            ModuleStrategy strategy = moduleStrategyFactory.getStrategy(5);
            return strategy.getNextQuestion(5L, 0, 1);
        }
        // 判断是从哪个模块跳到9模块的
        if (moduleCount >= 3) {
            // 跳回原来的模块  1-1,2   2-3,4     3-5,6    4-7,8
            if (index == 2) {
                // 做完第一模块，跳第二模块
                ModuleStrategy strategy = moduleStrategyFactory.getStrategy(2);
                return strategy.getNextQuestion(2L, 0, 1);
            } else if (index == 4) {
                // 做完第二模块，跳第三模块
                ModuleStrategy strategy = moduleStrategyFactory.getStrategy(3);
                return strategy.getNextQuestion(3L, 0, 1);
            } else if (index == 6) {
                // 做完第三模块，跳第四模块
                ModuleStrategy strategy = moduleStrategyFactory.getStrategy(4);
                return strategy.getNextQuestion(4L, 0, 1);
            }
        }
        Object o = redisService.lGetIndex(key, Func.toLong(index));
        QuestionVO convert = null;
        if (ObjectUtil.isNotEmpty(o)) {
            convert = Func.convert(o, QuestionVO.class);
        } else {
            // 重新刷入题库
            setDateToRedis(moduleId.intValue());
            o = redisService.lGetIndex(key, Func.toLong(index));
            convert = Func.convert(o, QuestionVO.class);
        }
        convert.setModuleIndex(index);
        convert.setModuleCount(++moduleCount);
        // 判断是否还有下一题
        if ((index + 1) >= size) {
            // 没有下一题
            convert.setIsNextId(0);
        }
        return convert;
    }
}
