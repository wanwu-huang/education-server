package org.mentpeak.user.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import org.mentpeak.user.entity.Job;
import org.mentpeak.user.mapper.JobMapper;
import org.mentpeak.user.service.IJobService;
import org.mentpeak.user.vo.JobVO;
import org.springframework.stereotype.Service;

/**
 * 职业表 服务实现类
 *
 * @author lxp
 * @since 2021-03-27
 */
@Service
public class JobServiceImpl extends ServiceImpl < JobMapper, Job > implements IJobService {

    @Override
    public IPage < JobVO > selectJobPage (
            IPage < JobVO > page ,
            JobVO job ) {
        return page.setRecords ( baseMapper.selectJobPage ( page ,
                                                            job ) );
    }

}
