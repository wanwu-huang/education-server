package org.mentpeak.test.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.mentpeak.core.tenant.mp.TenantEntity;

/**
 * 测评任务表实体类
 *
 * @author lxp
 * @since 2022-07-12
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "TestTask对象", description = "测评任务表")
public class TestTask extends TenantEntity {

    private static final long serialVersionUID = 1L;


    @ApiModelProperty("主键id")
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 任务名称
     */
    @ApiModelProperty(value = "任务名称")
    private String taskName;
    /**
     * 任务开始时间
     */
    @ApiModelProperty(value = "任务开始时间")
    private String beginTime;
    /**
     * 任务结束时间
     */
    @ApiModelProperty(value = "任务结束时间")
    private String endTime;
    /**
     * 报告属性 【字典表】
     */
    @ApiModelProperty(value = "报告属性 【字典表】")
    private Integer reportIsVisible;
    /**
     * 测试途径ID
     */
    @ApiModelProperty(value = "测试途径ID")
    private Long testApproachId;


}
