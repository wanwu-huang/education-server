package org.mentpeak.user.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.time.LocalDateTime;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 职业表实体类
 *
 * @author lxp
 * @since 2021-03-27
 */
@Data
@TableName ( "t_his_job" )
@ApiModel ( value = "Job对象", description = "职业表" )
public class Job implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @ApiModelProperty ( value = "主键" )
    @TableId ( value = "id", type = IdType.ASSIGN_ID )
    private Long id;
    /**
     * 租户编号
     */
    @ApiModelProperty ( value = "租户编号" )
    private String tenantCode;
    /**
     * 创建人
     */
    @ApiModelProperty ( value = "创建人" )
    private Long createUser;
    /**
     * 创建时间
     */
    @ApiModelProperty ( value = "创建时间" )
    private LocalDateTime createTime;
    /**
     * 修改人
     */
    @ApiModelProperty ( value = "修改人" )
    private Long updateUser;
    /**
     * 修改时间
     */
    @ApiModelProperty ( value = "修改时间" )
    private LocalDateTime updateTime;
    /**
     * 状态(0:启用;1:禁用)
     */
    @ApiModelProperty ( value = "状态(0:启用;1:禁用)" )
    private Integer status;
    /**
     * 是否已删除
     */
    @ApiModelProperty ( value = "是否已删除" )
    private Integer isDeleted;
    /**
     * 职业名称
     */
    @ApiModelProperty ( value = "职业名称" )
    private String jobName;


}
