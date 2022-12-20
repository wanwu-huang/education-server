package org.mentpeak.user.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

import org.mentpeak.user.entity.Job;
import org.mentpeak.user.vo.JobVO;

/**
 * 职业表 服务类
 *
 * @author lxp
 * @since 2021-03-27
 */
public interface IJobService extends IService < Job > {

    /**
     * 自定义分页
     *
     * @param page
     * @param job
     * @return
     */
    IPage < JobVO > selectJobPage (
            IPage < JobVO > page ,
            JobVO job );

}
