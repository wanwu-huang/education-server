package org.mentpeak.test.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.mentpeak.core.mybatisplus.base.BaseEntity;

import java.time.LocalDateTime;

/**
 * 教师评定试卷信息表实体类
 *
 * @author lxp
 * @since 2022-08-15
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "TestTeacherPaper对象", description = "教师评定试卷信息表")
public class TestTeacherPaper extends BaseEntity {

    private static final long serialVersionUID = 1L;


    /**
     * 主键
     */
    @ApiModelProperty ( value = "主键" )
    @TableId( value = "id", type = IdType.ASSIGN_ID )
    private Long id;

    /**
     * 租户编号
     */
    @ApiModelProperty(value = "租户编号")
    private String tenantCode;


    /**
     * 问卷ID
     */
    @ApiModelProperty(value = "问卷ID")
      private Long questionnaireId;
    /**
     * 用户ID
     */
    @ApiModelProperty(value = "用户ID")
      private Long userId;
    /**
     * 开始答卷时间
     */
    @ApiModelProperty(value = "开始答卷时间")
      private LocalDateTime startTime;
    /**
     * 结束答卷时间
     */
    @ApiModelProperty(value = "结束答卷时间")
      private LocalDateTime finishTime;
    /**
     * 当前作答题干 ID
     */
    @ApiModelProperty(value = "当前作答题干 ID")
      private Long questionId;
    /**
     * 是否完成当前作答题干 (0未完成 1完成)
     */
    @ApiModelProperty(value = "是否完成当前作答题干 (0未完成 1完成)")
      private Integer isFinishQuestion;
    /**
     * 是否完成此试卷(0未完成 1完成 2 保存未提交)
     */
    @ApiModelProperty(value = "是否完成此试卷(0未完成 1完成 2 保存未提交)")
      private Integer isFinish;
    /**
     * 评价记录ID
     */
    @ApiModelProperty(value = "评价记录ID")
      private String evaluationRecordsId;
    /**
     * 备注
     */
    @ApiModelProperty(value = "备注")
      private String remarks;

    @ApiModelProperty(value = "班级ID")
    private Long classId;

    @ApiModelProperty(value = "年级ID")
    private Long gradeId;


}
