package org.mentpeak.test.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.mentpeak.test.entity.ClassUser;
import org.mentpeak.test.vo.*;

import java.util.List;

/**
 * 班级用户表 Mapper 接口
 *
 * @author lxp
 * @since 2022-07-12
 */
public interface ClassUserMapper extends BaseMapper<ClassUser> {

	/**
	 * 自定义分页
	 *
	 * @param page
	 * @param classUser
	 * @return
	 */
	List<ClassUserVO> selectClassUserPage(IPage page, ClassUserVO classUser);

	/**
	 * 用户管理列表
	 * @param page page
	 * @param gradeId 一级部门ID
	 * @return info
	 */
	Page<GradeUserVO> userManagerList(IPage<GradeUserVO> page, @Param("gradeId") Long gradeId);

	/**
	 * 一级部门详情列表
	 * @param page page
	 * @param gradeId 一级部门ID
	 * @return info
	 */
	Page<GradeDetailUserVO> userManagerDetailList(IPage<GradeDetailUserVO> page, @Param("gradeId") Long gradeId,
												  @Param("realName") String realName, @Param("account") String account,
												  @Param("classId") Long classId, @Param("sex") Integer sex);

	/**
	 * 获取班级 年级信息
	 * @param id
	 * @return
	 */
	DictVO getValueById(@Param("id") Long id);

    /**
     * 查询年级下完成任务的数量
     * @param gradeId
     * @return
     */
	Integer taskCountByGradeId(@Param("gradeId") Long gradeId);

	/**
	 * 根据年级ID获取用户ID
	 * @param gradeId
	 * @return
	 */
	List<Long> userIdListByGradeId(@Param("gradeId") String gradeId);

	/**
	 * 根据用户ID 修改班级年级信息
	 * @param ids
	 * @return
	 */
	int updateClassGradeInfoByUserId(List<Long> ids);

	/**
	 * 根据用户ID 修改用户信息
	 * @param ids
	 * @return
	 */
	int updateUserInfoByUserId(List<Long> ids);
}
