package org.mentpeak.core.log.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import org.mentpeak.core.log.mapper.LogErrorMapper;
import org.mentpeak.core.log.model.LogError;
import org.mentpeak.core.log.service.ILogErrorService;
import org.springframework.stereotype.Service;

/**
 * 服务实现类
 */
@Service
public class LogErrorServiceImpl extends ServiceImpl < LogErrorMapper, LogError > implements ILogErrorService {

}
