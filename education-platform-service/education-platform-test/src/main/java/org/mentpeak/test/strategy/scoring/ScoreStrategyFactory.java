package org.mentpeak.test.strategy.scoring;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 维度计分策略工厂
 * @author demain_lee
 * @since 2022-08-09
 */
@Component
public class ScoreStrategyFactory implements InitializingBean, ApplicationContextAware {

    private static final Map<Long, DimensionStrategy> STRATEGY_MAP = new HashMap<>();

    private ApplicationContext applicationContext;

    /**
     * 根据类型获取对应的处理器
     */
    public DimensionStrategy getStrategy(Long type) {
        return STRATEGY_MAP.get(type);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        // 将 Spring 容器中所有的 ModuleStrategy 注册到 STRATEGY_MAP
        applicationContext.getBeansOfType(DimensionStrategy.class).values().forEach(
                handler -> STRATEGY_MAP.put(handler.type(), handler)
        );
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
