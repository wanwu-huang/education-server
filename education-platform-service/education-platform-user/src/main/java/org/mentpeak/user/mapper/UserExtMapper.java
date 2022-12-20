package org.mentpeak.user.mapper;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Param;
import org.mentpeak.user.entity.User;
import org.mentpeak.user.entity.UserExt;
import org.mentpeak.user.entity.UserExtAndSchool;
import org.mentpeak.user.vo.UserExtVO;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户信息扩展表 Mapper 接口
 *
 * @author lxp
 * @since 2021-03-27
 */
public interface UserExtMapper extends BaseMapper<UserExt> {

    /**
     * 自定义分页
     *
     * @param page
     * @param userExt
     * @return
     */
    List<UserExtVO> selectUserExtPage(
            IPage page,
            UserExtVO userExt);

    User getUserByAccount(@Param("account") String account, @Param("address") String address,
                          @Param("gradeId") Long gradeId, @Param("classId") Long classId);

    /**
     * 根据用户ID查询已删除数据
     *
     * @param userId
     * @return
     */
    List<UserExt> userExtList(@Param("userId") Long userId);

    /**
     * 更新数据
     *
     * @param id
     * @param grade
     * @param classId
     * @param updateTime
     */
    void updateUserExt(@Param("id") Long id, @Param("grade") String grade
            , @Param("classId") Long classId, @Param("updateTime") LocalDateTime updateTime);

    /**
     * 获取用户拓展信息 包含逻辑删除
     *
     * @param userId
     * @return
     */
    UserExt userExtInfoById(@Param("userId") Long userId);

    @InterceptorIgnore(tenantLine = "true")
    UserExtAndSchool getTenantName(@Param("userId") Long userId);

    @InterceptorIgnore(tenantLine = "true")
    void updateSchool(@Param("userId") Long userId, @Param("address") String address);
}
