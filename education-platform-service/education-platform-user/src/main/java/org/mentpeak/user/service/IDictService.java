package org.mentpeak.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.mentpeak.dict.entity.Dict;
import org.mentpeak.dict.vo.GradeClassVO;
import org.mentpeak.dict.vo.GradeVO;
import org.mentpeak.user.dto.BindStudentDTO;
import org.mentpeak.user.vo.ParentStudentVO;

import java.util.List;

/**
 * 职业表 服务类
 *
 * @author lxp
 * @since 2021-03-27
 */
public interface IDictService extends IService<Dict> {

    /**
     * 获取年级列表
     *
     * @return java.util.List<org.mentpeak.dict.vo.GradeVO>
     * @author hzl
     * @date 2022/6/27 15:41
     */
    List<GradeVO> getGradeList();

    /**
     * 获取班级列表
     *
     * @param id 年级id
     * @return java.util.List<org.mentpeak.dict.vo.GradeVO>
     * @author hzl
     * @date 2022/6/27 15:42
     */
    List<GradeVO> getClassList(Long id);

    /**
     * 绑定学生信息
     * @author hzl
     * @date 2022/6/28 10:48
     * @param bindStudentDTO
     * @return boolean
     */
    boolean bindStudent(BindStudentDTO bindStudentDTO);

    List<ParentStudentVO> bindStudentList();

    boolean delBindStudent(Integer studentId);


    /**
     * 获取字典表
     *
     * @param code 字典编号
     * @return list
     */
    List<Dict> getList ( String code );

    /**
     * 根据年级名字 班级名字查询信息
     * @param gradeName 年级名字
     * @param className 班级名字
     * @return 信息
     */
    List<GradeClassVO> listByGradeAndClassName(String gradeName,String className);

    /**
     * 根据年级名字 查询信息
     * @param gradeName 年级名字
     * @return 信息
     */
    List<Dict> listByGradeName(String gradeName);
}
