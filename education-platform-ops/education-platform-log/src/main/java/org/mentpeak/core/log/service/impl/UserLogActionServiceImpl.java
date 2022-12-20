package org.mentpeak.core.log.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import org.mentpeak.core.log.entity.UserLogAction;
import org.mentpeak.core.log.mapper.UserLogActionMapper;
import org.mentpeak.core.log.service.IUserLogActionService;
import org.mentpeak.core.log.vo.UserLogActionVO;
import org.springframework.stereotype.Service;

/**
 * 管理后台-操作日志表 服务实现类
 *
 * @author lxp
 * @since 2021-03-27
 */
@Service
public class UserLogActionServiceImpl extends ServiceImpl < UserLogActionMapper, UserLogAction > implements IUserLogActionService {

    @Override
    public IPage < UserLogActionVO > selectUserLogActionPage (
            IPage < UserLogActionVO > page ,
            UserLogActionVO userLogAction ) {
        return page.setRecords ( baseMapper.selectUserLogActionPage ( page ,
                                                                      userLogAction ) );
    }

}
