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
 * 教师评定问卷测试记录表实体类
 *
 * @author lxp
 * @since 2022-08-15
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "TestTeacherPaperRecord对象", description = "教师评定问卷测试记录表")
public class TestTeacherPaperRecord extends BaseEntity {

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
     * 试卷ID
     */
    @ApiModelProperty(value = "试卷ID")
      private Long paperId;
    /**
     * 学生ID
     */
    @ApiModelProperty(value = "学生ID")
      private Long stuId;
    /**
     * 用户ID
     */
    @ApiModelProperty(value = "用户ID")
      private Long userId;
    /**
     * 分数
     */
    @ApiModelProperty(value = "分数")
      private Integer score;
    /**
     * 开始测试时间
     */
    @ApiModelProperty(value = "开始测试时间")
      private LocalDateTime startTime;
    /**
     * 结束测试时间
     */
    @ApiModelProperty(value = "结束测试时间")
      private LocalDateTime finishTime;
    /**
     * 风险等级
     */
    @ApiModelProperty(value = "风险等级")
      private Integer riskLevel;
    /**
     * 测试结果
     */
    @ApiModelProperty(value = "测试结果")
      private String riskResult;


}
