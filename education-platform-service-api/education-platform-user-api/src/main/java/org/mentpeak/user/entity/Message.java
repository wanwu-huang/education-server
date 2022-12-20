package org.mentpeak.user.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.time.LocalDateTime;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 实体类
 *
 * @author mp
 */
@Data
@TableName ( "platform_message" )
@ApiModel ( value = "Message对象", description = "Message对象" )
public class Message implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @ApiModelProperty ( value = "主键" )
    @TableId ( value = "id", type = IdType.AUTO )
    private Integer id;

    /**
     * 租户编号
     */
    @ApiModelProperty ( value = "租户编号" )
    private String tenantCode;

    /**
     * 验证码
     */
    @ApiModelProperty ( value = "验证码" )
    private String code;

    /**
     * 手机号
     */
    @ApiModelProperty ( value = "手机号" )
    private String mobile;

    /**
     * 验证码类型
     */
    @ApiModelProperty ( value = "验证码类型" )
    @TableField ( "`type`" )
    private Integer type;

    /**
     * 状态 0 未验证 1 已验证 2 失效
     */
    @ApiModelProperty ( value = "状态 0 未验证 1 已验证 2 失效" )
    @TableField ( "`status`" )
    private Integer status;
    /**
     * 生成时间
     */
    @ApiModelProperty ( value = "生成时间" )
    private LocalDateTime generateTime;
    /**
     * 验证时间
     */
    @ApiModelProperty ( value = "验证时间" )
    private LocalDateTime validateTime;
}
