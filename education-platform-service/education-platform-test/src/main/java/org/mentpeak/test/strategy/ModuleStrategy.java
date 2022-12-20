package org.mentpeak.test.strategy;

import org.mentpeak.test.vo.QuestionVO;

/*
 * 模糊策略接口
 * @author hzl
 * @date 2022/7/14 14:34
 * @param null
 * @return null
 */
public interface ModuleStrategy {

    /*
     * 类型
     * @author hzl
     * @date 2022/7/14 14:34
     * @return java.lang.Integer
     */
    Integer type();

    /*
     * 指定模块刷入redis
     * @author hzl
     * @date 2022/7/14 14:35
     * @param type
     * @return boolean
     */
    boolean setDateToRedis(Integer type);

    /**
     * 根据模块获取下一题
     *
     * @param moduleId
     * @param index
     * @return
     */
    QuestionVO getNextQuestion(Long moduleId, int index, int moduleCount);
}
