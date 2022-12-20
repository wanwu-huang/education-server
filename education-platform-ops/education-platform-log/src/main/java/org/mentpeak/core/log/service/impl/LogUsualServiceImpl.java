package org.mentpeak.core.log.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import org.mentpeak.core.log.mapper.LogUsualMapper;
import org.mentpeak.core.log.model.LogUsual;
import org.mentpeak.core.log.service.ILogUsualService;
import org.springframework.stereotype.Service;

/**
 * 服务实现类
 */
@Service
public class LogUsualServiceImpl extends ServiceImpl < LogUsualMapper, LogUsual > implements ILogUsualService {

}
