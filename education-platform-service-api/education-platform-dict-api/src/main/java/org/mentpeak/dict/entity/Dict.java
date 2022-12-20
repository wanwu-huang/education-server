
package org.mentpeak.dict.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 实体类
 *
 * @author lxp
 */
@Data
@TableName ( "platform_dict" )
@ApiModel ( value = "Dict对象", description = "Dict对象" )
public class Dict implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @ApiModelProperty ( value = "主键" )
    @TableId ( value = "id", type = IdType.ASSIGN_ID )
    private Long id;

    /**
     * 父主键
     */
    @ApiModelProperty ( value = "父主键" )
    private Long parentId;

    /**
     * 字典码
     */
    @ApiModelProperty ( value = "字典码" )
    private String code;

    /**
     * 字典值
     */
    @ApiModelProperty ( value = "字典值" )
    private Integer dictKey;

    /**
     * 字典名称
     */
    @ApiModelProperty ( value = "字典名称" )
    private String dictValue;

    /**
     * 排序
     */
    @ApiModelProperty ( value = "排序" )
    private Integer sort;

    /**
     * 字典备注
     */
    @ApiModelProperty ( value = "字典备注" )
    private String remark;

    /**
     * 是否已删除
     */
    @TableLogic
    @ApiModelProperty ( value = "是否已删除" )
    private Integer isDeleted;


}
