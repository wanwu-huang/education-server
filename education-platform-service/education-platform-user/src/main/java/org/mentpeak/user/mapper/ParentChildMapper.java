package org.mentpeak.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.mentpeak.dict.entity.Dict;
import org.mentpeak.dict.entity.ParentChild;
import org.mentpeak.dict.vo.GradeVO;

import java.util.List;

/**
 * 职业表 Mapper 接口
 *
 * @author lxp
 * @since 2021-03-27
 */
public interface ParentChildMapper extends BaseMapper<ParentChild> {

}
