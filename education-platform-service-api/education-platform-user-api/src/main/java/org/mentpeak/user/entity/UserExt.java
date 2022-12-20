package org.mentpeak.user.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.time.LocalDateTime;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 用户信息扩展表实体类
 *
 * @author lxp
 * @since 2021-03-27
 */
@Data
@TableName ( "t_his_user_ext" )
@ApiModel ( value = "UserExt对象", description = "用户信息扩展表" )
public class UserExt implements Serializable {

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
    @ApiModelProperty ( value = "学生id" )
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
    @TableLogic
    private Integer isDeleted;
    /**
     * 职业ID
     */
    @ApiModelProperty ( value = "职业ID" )
    private Long jobId;
    /**
     * 头像地址
     */
    @ApiModelProperty ( value = "头像地址" )
    private String headUrl;
    /**
     * 所在地ID
     */
    @ApiModelProperty ( value = "所在地ID" )
    private Long areaId;
    /**
     * 学校/单位地址
     */
    @ApiModelProperty ( value = "学校" )
    private String address;

    /**
     * 备注
     */
    @ApiModelProperty ( value = "备注" )
    private String remark;

    /**
     * 年级
     */
    @ApiModelProperty ( value = "年级" )
    private String grade;

    /**
     * 班级ID
     */
    @ApiModelProperty ( value = "班级ID" )
    private Long classId;

    /**
     * 紧急联系人
     */
    @ApiModelProperty ( value = "紧急联系人" )
    private String contactPerson;

    /**
     * 紧急联系人手机号
     */
    @ApiModelProperty ( value = "紧急联系人手机号" )
    private String contactPersonPhone;

    /**
     * 干预状态 （参考字典表  1 无需干预  2 待干预   3  已转介  4 已干预）
     */
    @ApiModelProperty ( value = "干预状态" )
    private Integer interveneStatus;

    @ApiModelProperty(value = "身份证")
    private String idCard;
}
