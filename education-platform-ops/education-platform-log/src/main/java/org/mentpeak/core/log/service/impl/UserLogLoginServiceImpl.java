package org.mentpeak.core.log.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import org.mentpeak.core.log.entity.UserLogLogin;
import org.mentpeak.core.log.mapper.UserLogLoginMapper;
import org.mentpeak.core.log.service.IUserLogLoginService;
import org.mentpeak.core.log.vo.UserLogLoginVO;
import org.springframework.stereotype.Service;

/**
 * 用户错误登录日志 服务实现类
 *
 * @author lxp
 * @since 2021-03-27
 */
@Service
public class UserLogLoginServiceImpl extends ServiceImpl < UserLogLoginMapper, UserLogLogin > implements IUserLogLoginService {

    @Override
    public IPage < UserLogLoginVO > selectUserLogLoginPage (
            IPage < UserLogLoginVO > page ,
            UserLogLoginVO userLogLogin ) {
        return page.setRecords ( baseMapper.selectUserLogLoginPage ( page ,
                                                                     userLogLogin ) );
    }

}
