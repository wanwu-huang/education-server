package org.mentpeak.common.constant;

/**
 * 业务角色
 */
public class RoleConstant {

	public static final String ADMINISTRATOR = "administrator";

	/**
	 * 超级管理员
	 */
	public static final String ADMIN = "admin";

	/**
	 * 老师
	 */
	public static final String TEACHER = "teacher";

	/**
	 * 学生
	 */
	public static final String STUDENT = "student";

	/**
	 * 年级组长
	 */
	public static final String GRADE_LEADER = "grade_leader";

	/**
	 * 心理老师
	 */
	public static final String PSYCHOLOGICAL_TEACHER = "psychological_teacher";

	/**
	 * 德育老师
	 */
	public static final String MORAL_TEACHER = "moral_teacher";


	/**
	 * 老师端 包括管理员
	 */
	public static final String HAS_ADMIN_TEACH = "hasAnyRole('administrator', 'admin','teacher','grade_leader','psychological_teacher','moral_teacher')";


	/**
	 * 学员端
	 */
	public static final String HAS_STUDENT = "hasRole('student')";

}
