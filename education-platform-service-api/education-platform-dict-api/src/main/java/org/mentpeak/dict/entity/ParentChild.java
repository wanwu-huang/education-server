
package org.mentpeak.dict.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 实体类
 *
 * @author lxp
 */
@Data
@TableName("parent_child")
@ApiModel(value = "ParentChild对象", description = "ParentChild对象")
public class ParentChild implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 家长id
     */
    @ApiModelProperty(value = "家长id")
    private Long parentId;

    /**
     * 学生id
     */
    @ApiModelProperty(value = "学生id")
    private Long childId;


}
