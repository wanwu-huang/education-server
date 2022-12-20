package org.mentpeak.core.log.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;

import org.mentpeak.core.log.entity.UserLogLogin;
import org.mentpeak.core.log.vo.UserLogLoginVO;

import java.util.List;

/**
 * 用户错误登录日志 Mapper 接口
 *
 * @author lxp
 * @since 2021-03-27
 */
public interface UserLogLoginMapper extends BaseMapper < UserLogLogin > {

    /**
     * 自定义分页
     *
     * @param page
     * @param userLogLogin
     * @return
     */
    List < UserLogLoginVO > selectUserLogLoginPage (
		    IPage page ,
		    UserLogLoginVO userLogLogin );

}
