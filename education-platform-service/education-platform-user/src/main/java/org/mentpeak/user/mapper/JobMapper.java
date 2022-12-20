package org.mentpeak.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;

import org.mentpeak.user.entity.Job;
import org.mentpeak.user.vo.JobVO;

import java.util.List;

/**
 * 职业表 Mapper 接口
 *
 * @author lxp
 * @since 2021-03-27
 */
public interface JobMapper extends BaseMapper < Job > {

    /**
     * 自定义分页
     *
     * @param page
     * @param job
     * @return
     */
    List < JobVO > selectJobPage (
            IPage page ,
            JobVO job );

}
