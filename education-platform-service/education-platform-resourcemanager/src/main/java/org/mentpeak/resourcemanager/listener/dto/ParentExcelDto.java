package org.mentpeak.resourcemanager.listener.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.mentpeak.resourcemanager.listener.convert.QuestionTypeCovert;

import java.io.Serializable;

/**
 * @author lxp
 * @date 2022/06/18 22:40
 **/
@Data
public class ParentExcelDto implements Serializable {

	private static final long serialVersionUID = 1L;
	/**
	 * 父题干ID
	 */
	@ApiModelProperty(value = "题干ID")
	@ExcelProperty(value = "题干序号")
	private Long qId;

	/**
	 * 父题干ID
	 */
	@ApiModelProperty(value = "父题干ID")
	@ExcelProperty(value = "父题干序号")
	private Long parentId;
	/**
	 * 问卷ID
	 */
	@ApiModelProperty(value = "问卷ID")
	@ExcelProperty(value = "问卷序号")
	private Long questionnaireId;
	/**
	 * 题目内容
	 */
	@ApiModelProperty(value = "题目内容")
	@ExcelProperty(value = "题干标题")
	private String title;
	/**
	 * 题型
	 */
	@ApiModelProperty(value = "题型")
	@ExcelProperty(value = "题型",converter = QuestionTypeCovert.class)
	private Long questionType;
//	/**
//	 * 排序
//	 */
//	@ApiModelProperty(value = "排序")
//	private Integer sort;

	/**
	 * ==============================选项==============================
	 */
//	/**
//	 * 题支的题序
//	 */
//	@ApiModelProperty(value = "题支的题序")
//	private Integer osort;
	/**
	 * 题支内容
	 */
	@ApiModelProperty(value = "题支内容")
	@ExcelProperty(value = "题支内容")
	private String otitle;
	/**
	 * 得分
	 */
	@ApiModelProperty(value = "得分")
	@ExcelProperty(value = "计分")//,converter = NullToIntCovert.class)
	private String score;
	/**
	 * 选项属性扩展
	 */
	@ApiModelProperty(value = "选项属性扩展")
	@ExcelProperty(value = "选项属性扩展1")
	private String extParam;

}
