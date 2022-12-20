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
 * @author hzl
 * @create 2021-04-08
 */

@Data
@TableName ( "platform_param" )
@ApiModel ( value = "userParam对象", description = "系统参数表" )
public class UserParam implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @ApiModelProperty ( value = "主键" )
    @TableId ( value = "id", type = IdType.ASSIGN_ID )
    private Long id;
    /**
     * 参数名
     */
    @ApiModelProperty ( value = "参数名" )
    private String paramName;
    /**
     * 参数键
     */
    @ApiModelProperty ( value = "参数键" )
    private String paramKey;
    /**
     * 参数值
     */
    @ApiModelProperty ( value = "参数值" )
    private String paramValue;
    /**
     * 备注
     */
    @ApiModelProperty ( value = "备注" )
    private String remark;
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
    @TableField ( "`status`" )
    private Integer status;
    /**
     * 是否已删除
     */
    @ApiModelProperty ( value = "是否已删除" )
    private Integer isDeleted;

}
