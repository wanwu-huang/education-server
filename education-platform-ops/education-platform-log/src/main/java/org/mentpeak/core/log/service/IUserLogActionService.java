package org.mentpeak.core.log.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

import org.mentpeak.core.log.entity.UserLogAction;
import org.mentpeak.core.log.vo.UserLogActionVO;

/**
 * 管理后台-操作日志表 服务类
 *
 * @author lxp
 * @since 2021-03-27
 */
public interface IUserLogActionService extends IService < UserLogAction > {

    /**
     * 自定义分页
     *
     * @param page
     * @param userLogAction
     * @return
     */
    IPage < UserLogActionVO > selectUserLogActionPage (
		    IPage < UserLogActionVO > page ,
		    UserLogActionVO userLogAction );

}
