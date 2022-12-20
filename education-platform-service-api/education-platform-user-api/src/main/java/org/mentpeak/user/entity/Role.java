package org.mentpeak.user.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.ibatis.type.Alias;

import java.io.Serializable;

/**
 * @author hzl
 * @data 2022年01月07日10:33
 */
@Data
@TableName("platform_role")
@ApiModel(value = "role对象", description = "角色表")
@Alias("platformRoles")
public class Role implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;
    /**
     * 租户编号
     */
    @ApiModelProperty(value = "租户编号")
    private String tenantCode;
    /**
     * 父主键
     */
    @ApiModelProperty(value = "父主键")
    private Long parentId;
    /**
     * 角色名
     */
    @ApiModelProperty(value = "角色名")
    private String roleName;
    /**
     * 排序
     */
    @ApiModelProperty(value = "排序")
    private Integer sort;
    /**
     * 角色别名
     */
    @ApiModelProperty(value = "角色别名")
    private String roleAlias;
    /**
     * 是否已删除
     */
    @ApiModelProperty(value = "是否已删除")
    private Integer isDeleted;

}
