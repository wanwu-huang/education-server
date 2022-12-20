package org.mentpeak.parent.mapper;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.mentpeak.parent.entity.ParentPaper;

/**
 * 家长他评问卷题目信息表 Mapper 接口
 *
 * @author lxp
 * @since 2022-06-17
 */
public interface ParentPaperMapper extends BaseMapper<ParentPaper> {

    @InterceptorIgnore(tenantLine = "true")
    String getStudentName(@Param("userId") Long userId);

}
