package org.mentpeak.user.mapper;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.mentpeak.dict.entity.Dict;
import org.mentpeak.dict.vo.GradeClassVO;
import org.mentpeak.dict.vo.GradeVO;
import org.mentpeak.user.vo.ParentStudentVO;

import java.util.List;

/**
 * 职业表 Mapper 接口
 *
 * @author lxp
 * @since 2021-03-27
 */
public interface DictMapper extends BaseMapper<Dict> {

    // 根据年级id查询班级
    List<GradeVO> getClassById(@Param("id") Long id);

    @InterceptorIgnore(tenantLine = "true")
    List<ParentStudentVO> bindStudentList(@Param("parentId") Long parentId);

    /**
     * 获取字典表
     *
     * @param code 字典编号
     * @return list
     */
    List<Dict> getList(String code);

    /**
     * 根据年级名字 班级名字查询信息
     *
     * @param gradeName 年级名字
     * @param className 班级名字
     * @return 信息
     */
    List<GradeClassVO> listByGradeAndClassName(@Param("gradeName") String gradeName, @Param("className") String className);

    /**
     * 根据年级名字 班级名字查询信息
     *
     * @param gradeId   年级Id
     * @param className 班级名字
     * @return 信息
     */
    List<GradeClassVO> listByClassNameAndGradeId(@Param("gradeId") Long gradeId, @Param("className") String className);

}
