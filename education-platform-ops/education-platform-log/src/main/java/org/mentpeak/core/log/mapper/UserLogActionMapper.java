package org.mentpeak.core.log.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;

import org.mentpeak.core.log.entity.UserLogAction;
import org.mentpeak.core.log.vo.UserLogActionVO;

import java.util.List;

/**
 * 管理后台-操作日志表 Mapper 接口
 *
 * @author lxp
 * @since 2021-03-27
 */
public interface UserLogActionMapper extends BaseMapper < UserLogAction > {

    /**
     * 自定义分页
     *
     * @param page
     * @param userLogAction
     * @return
     */
    List < UserLogActionVO > selectUserLogActionPage (
		    IPage page ,
		    UserLogActionVO userLogAction );

}
