package org.mentpeak.core.log.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import org.mentpeak.core.log.mapper.LogApiMapper;
import org.mentpeak.core.log.model.LogApi;
import org.mentpeak.core.log.service.ILogApiService;
import org.springframework.stereotype.Service;

/**
 * 服务实现类
 */
@Service
public class LogApiServiceImpl extends ServiceImpl < LogApiMapper, LogApi > implements ILogApiService {


}
