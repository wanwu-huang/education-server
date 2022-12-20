package org.mentpeak.user.entity;/**
 * @author hzl
 * @create 2021-04-07
 */

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.time.LocalDateTime;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author hzl
 * @data 2021年04月07日11:39
 */
@Data
@TableName ( "sys_province" )
@ApiModel ( value = "SysProvince对象", description = "省级" )
public class SysProvince implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 主键
     */
    @ApiModelProperty ( value = "主键" )
    @TableId ( value = "id", type = IdType.ASSIGN_ID )
    private Long id;
    /**
     * 地区（省）ID
     */
    @ApiModelProperty ( "地区（省）ID" )
    private Long provinceId;
    /**
     * 省份编码
     */
    @ApiModelProperty ( "省份编码" )
    private String provinceCode;
    /**
     * 省份名称
     */
    @ApiModelProperty ( "省份名称" )
    private String provinceName;
    /**
     * 创建人
     */
    @ApiModelProperty ( value = "创建人ID" )
    private Long createUid;
    /**
     * 创建时间
     */
    @ApiModelProperty ( value = "创建时间" )
    private LocalDateTime createTime;
    /**
     * 修改人
     */
    @ApiModelProperty ( value = "修改人" )
    private Long updateUid;
    /**
     * 修改时间
     */
    @ApiModelProperty ( value = "修改ID时间" )
    private LocalDateTime updateTime;
    /**
     * 状态(0:启用;1:禁用)
     */
    @ApiModelProperty ( value = "状态(0:启用;1:禁用)" )
    private Integer deleteStatus;
    /**
     * 创建id
     */
    @ApiModelProperty ( value = "创建id" )
    private Integer creatorId;
    /**
     * 修改id
     */
    @ApiModelProperty ( value = "修改id" )
    private Integer updateId;
}
