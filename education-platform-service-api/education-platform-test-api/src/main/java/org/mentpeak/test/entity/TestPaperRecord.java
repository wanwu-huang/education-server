package org.mentpeak.test.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.mentpeak.core.mybatisplus.base.BaseEntity;

import java.time.LocalDateTime;

/**
 * 用户问卷测试记录表实体类
 *
 * @author lxp
 * @since 2022-07-13
 */
@Data
@TableName("test_paper_record")
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "TestPaperRecord对象", description = "用户问卷测试记录表")
public class TestPaperRecord extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("主键id")
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 租户编号
     */
    @ApiModelProperty(value = "租户编号")
    private String tenantCode;

    /**
     * 用户ID
     */
    @ApiModelProperty(value = "用户ID")
    private Long userId;
    /**
     * 学号
     */
    @ApiModelProperty(value = "学号")
    private String userNo;
    /**
     * 姓名
     */
    @ApiModelProperty(value = "姓名")
    private String userName;
    /**
     * 问卷ID
     */
    @ApiModelProperty(value = "问卷ID")
    private Long questionnaireId;
    /**
     * 任务ID
     */
    @ApiModelProperty(value = "任务ID")
    private Long taskId;
    /**
     * 问卷ID
     */
    @ApiModelProperty(value = "问卷ID")
    private Long paperId;
    /**
     * 问卷名字
     */
    @ApiModelProperty(value = "问卷名字")
    private String questionnaireName;
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
     * 报告ID
     */
    @ApiModelProperty(value = "报告ID")
    private String reportId;
    /**
     * 风险等级  1 低风险 2 中风险 3 高风险
     */
    @ApiModelProperty(value = "风险等级  1 低风险 2 中风险 3 高风险")
    private Integer riskLevel;
    /**
     * 测试结果
     */
    @ApiModelProperty(value = "测试结果")
    private String riskResult;
    /**
     * 干预状态 （ 1 无需干预  2 待干预   3  已转介  4 已干预 ）【预留】
     */
    @ApiModelProperty(value = "干预状态 （ 1 无需干预  2 待干预   3  已转介  4 已干预 ）【预留】")
    private Integer interveneStatus;


}
