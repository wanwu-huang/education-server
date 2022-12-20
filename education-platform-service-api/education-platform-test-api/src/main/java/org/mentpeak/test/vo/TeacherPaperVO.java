package org.mentpeak.test.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import lombok.Data;

/**
 * 教师评定信息
 * @author demain_lee
 * @since 2022-09-06
 */
@Data
@ApiModel(value = "TeacherPaperVO对象", description = "教师评定信息")
public class TeacherPaperVO implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "id")
	private Integer id;

	@ApiModelProperty(value = "年级ID")
	private Long gradeId;

	@ApiModelProperty(value = "班级ID")
	private Long classId;

	@ApiModelProperty(value = "开始时间")
	private String startTime;

	@ApiModelProperty(value = "状态")
	private String finishStatus;

	@ApiModelProperty(value = "是否完成此试卷(1完成(已提交) 2 保存未提交(暂时保存) 3 未评定)")
	private Integer isFinish;

	@ApiModelProperty(value = "年级名字")
	private String gradeName;

	@ApiModelProperty(value = "班级名字")
	private String className;

}
