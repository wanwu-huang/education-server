package org.mentpeak.core.log.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

import org.mentpeak.core.log.entity.UserLogLogin;
import org.mentpeak.core.log.vo.UserLogLoginVO;

/**
 * 用户错误登录日志 服务类
 *
 * @author lxp
 * @since 2021-03-27
 */
public interface IUserLogLoginService extends IService < UserLogLogin > {

    /**
     * 自定义分页
     *
     * @param page
     * @param userLogLogin
     * @return
     */
    IPage < UserLogLoginVO > selectUserLogLoginPage (
		    IPage < UserLogLoginVO > page ,
		    UserLogLoginVO userLogLogin );

}
