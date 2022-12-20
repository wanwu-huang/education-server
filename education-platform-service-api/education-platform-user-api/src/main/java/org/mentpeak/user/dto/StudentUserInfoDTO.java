package org.mentpeak.user.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 学生用户信息
 */
@Data
public class StudentUserInfoDTO {


	@ApiModelProperty(value = "学生姓名")
	@ExcelProperty(index = 0,value = "学生姓名")
	private String name;

	@ApiModelProperty(value = "性别")
	@ExcelProperty(index = 1,value = "性别")
	private String sex;

	@ApiModelProperty(value = "年级")
	@ExcelProperty(index = 2,value = "年级")
	private String gradeName;

	@ApiModelProperty(value = "班级")
	@ExcelProperty(index = 3,value = "班级")
	private String className;

	@ApiModelProperty(value = "学籍号")
	@ExcelProperty(index = 4,value = "学籍号")
	private String studentNo;

	@ApiModelProperty(value = "班主任姓名")
	@ExcelProperty(index = 5,value = "班主任姓名")
	private String teacherName;

	@ApiModelProperty(value = "手机号")
	@ExcelProperty(index = 6,value = "手机号")
	private String phone;

	@ApiModelProperty(value = "班主任身份证号码")
	@ExcelProperty(index = 7,value = "班主任身份证号码")
	private String idCard;

}
