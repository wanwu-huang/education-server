package org.mentpeak.test.strategy;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 模块策略工厂
 *
 * @author hzl
 * @date 2022/7/14 14:37
 * @return null
 */
@Component
public class ModuleStrategyFactory implements InitializingBean, ApplicationContextAware {

    private static final Map<Integer, ModuleStrategy> STRATEGY_MAP = new HashMap<>();

    private ApplicationContext applicationContext;

    /**
     * 根据类型获取对应的处理器
     *
     * @param type
     * @return org.mentpeak.test.strategy.ModuleStrategy
     * @author hzl
     * @date 2022/7/14 14:42
     */
    public ModuleStrategy getStrategy(Integer type) {
        return STRATEGY_MAP.get(type);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        // 将 Spring 容器中所有的 ModuleStrategy 注册到 STRATEGY_MAP
        applicationContext.getBeansOfType(ModuleStrategy.class).values().forEach(
                handler -> STRATEGY_MAP.put(handler.type(), handler)
        );
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
