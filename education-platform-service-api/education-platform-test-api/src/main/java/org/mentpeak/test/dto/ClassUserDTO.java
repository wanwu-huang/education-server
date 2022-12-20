package org.mentpeak.test.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 班级用户表数据传输对象实体类
 *
 * @author lxp
 * @since 2022-07-12
 */
@Data
public class ClassUserDTO{


	@ApiModelProperty(value = "学生姓名")
	@ExcelProperty(index = 1)
	private String name;

	@ApiModelProperty(value = "性别")
	@ExcelProperty(index = 2)
	private String sex;

	@ApiModelProperty(value = "年级")
	@ExcelProperty(index = 3)
	private String gradeName;

	@ApiModelProperty(value = "班级")
	@ExcelProperty(index = 4)
	private String className;

	@ApiModelProperty(value = "学籍号")
	@ExcelProperty(index = 5)
	private String studentNo;

	@ApiModelProperty(value = "班主任姓名")
	@ExcelProperty(index = 6)
	private String teacherName;

	@ApiModelProperty(value = "手机号")
	@ExcelProperty(index = 7)
	private String phone;

	@ApiModelProperty(value = "班主任身份证号码")
	@ExcelProperty(index = 8)
	private String idCard;

}
