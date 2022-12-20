package org.mentpeak.user.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

import org.mentpeak.core.tool.api.Result;
import org.mentpeak.user.entity.*;
import org.mentpeak.user.vo.SchoolVO;
import org.mentpeak.user.vo.UserExtVO;

import java.util.List;

/**
 * 用户信息扩展表 服务类
 *
 * @author lxp
 * @since 2021-03-27
 */
public interface IUserExtService extends IService < UserExt > {

    /**
     * 自定义分页
     *
     * @param page
     * @param userExt
     * @return
     */
    IPage < UserExtVO > selectUserExtPage (
            IPage < UserExtVO > page ,
            UserExtVO userExt );

    Result < List < SysProvince > > findPrivince ( );

    Result < List < SysCity > > findCity ( SysProvince province );

    Result < List < SysArea > > findArea ( SysCity city );

    Result findImgUrl ( );

    Result getAddress ( );



    /**
     * 根据市编码 查询省份信息
     *
     * @param cityCode
     * @return
     */
    SysProvince getProvince ( String cityCode );


}
