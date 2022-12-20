package org.mentpeak.test.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.mentpeak.test.dto.GradeDetailUserDTO;
import org.mentpeak.test.entity.ClassUser;
import org.mentpeak.test.vo.*;
import org.springframework.transaction.annotation.Transactional;

/**
 * 班级用户表 服务类
 *
 * @author lxp
 * @since 2022-07-12
 */
public interface IClassUserService extends IService<ClassUser> {

	/**
	 * 自定义分页
	 *
	 * @param page
	 * @param classUser
	 * @return
	 */
	IPage<ClassUserVO> selectClassUserPage(IPage<ClassUserVO> page, ClassUserVO classUser);

	/**
	 * 用户管理列表
	 * @param page page
	 * @param gradeId 一级部门ID
	 * @return info
	 */
	Page<GradeUserVO> userManagerList(IPage<GradeUserVO> page, Long gradeId);


	/**
	 * 一级部门详情列表
	 * @param page page
	 * @param gradeDetailUserDTO 条件
	 * @return info
	 */
	Page<GradeDetailUserVO> userManagerDetailList(IPage<GradeDetailUserVO> page, GradeDetailUserDTO gradeDetailUserDTO);


	/**
	 * 删除用户
	 * @param userId 用户ID
	 * @return boolean
	 */
	@Transactional(rollbackFor = Exception.class)
	Boolean deleteById(Long userId);

	/**
	 * 删除用户
	 * @param gradeId 一级部门ID
	 * @return boolean
	 */
	@Transactional(rollbackFor = Exception.class)
	Boolean deleteByGradeId(Long gradeId);

}
