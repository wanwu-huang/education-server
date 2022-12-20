package org.mentpeak.user.wrapper;

import org.mentpeak.core.mybatisplus.support.BaseEntityWrapper;
import org.mentpeak.core.tool.utils.BeanUtil;
import org.mentpeak.user.entity.Job;
import org.mentpeak.user.vo.JobVO;

import lombok.AllArgsConstructor;

/**
 * 职业表包装类,返回视图层所需的字段
 *
 * @author lxp
 * @since 2021-03-27
 */
@AllArgsConstructor
public class JobWrapper extends BaseEntityWrapper < Job, JobVO > {


    @Override
    public JobVO entityVO ( Job job ) {
        JobVO jobVO = BeanUtil.copy ( job ,
                                      JobVO.class );


        return jobVO;
    }

}
