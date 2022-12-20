package org.mentpeak.core.log.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.time.LocalDateTime;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 管理后台-操作日志表实体类
 *
 * @author lxp
 * @since 2021-03-27
 */
@Data
@TableName ( "t_his_user_log_action" )
@ApiModel ( value = "UserLogAction对象", description = "管理后台-操作日志表" )
public class UserLogAction implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @ApiModelProperty ( value = "主键" )
    @TableId ( value = "id", type = IdType.ASSIGN_ID )
    private Long id;
    /**
     * 创建时间
     */
    @ApiModelProperty ( value = "创建时间" )
    private LocalDateTime createTime;
    /**
     * 用户编号
     */
    @ApiModelProperty ( value = "用户编号" )
    private Long userId;
    /**
     * 客户端ID
     */
    @ApiModelProperty ( value = "客户端ID" )
    private String clientId;
    /**
     * 操作状态(1成功，0失败)
     */
    @ApiModelProperty ( value = "操作状态(1成功，0失败)" )
    private Integer actionStatus;
    /**
     * 操作IP
     */
    @ApiModelProperty ( value = "操作IP" )
    private String actionIp;
    /**
     * 操作详情
     */
    @ApiModelProperty ( value = "操作详情" )
    private String actionDetails;


}
