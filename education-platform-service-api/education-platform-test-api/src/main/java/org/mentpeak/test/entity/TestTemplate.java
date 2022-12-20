package org.mentpeak.test.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 导入用户模板实体类
 *
 * @author lxp
 * @since 2022-08-12
 */
@Data
@ApiModel(value = "TestTemplate对象", description = "导入用户模板")
public class TestTemplate implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 模板名称
     */
    @ApiModelProperty(value = "模板名称")
      private String name;
    /**
     * 地址URL
     */
    @ApiModelProperty(value = "地址URL")
      private String url;
    /**
     * 类别 0 学生 1 政企用户
     */
    @ApiModelProperty(value = "类别 0 学生 1 政企用户")
      private Integer type;
    /**
     * 备注
     */
    @ApiModelProperty(value = "备注")
      private String remark;
    /**
     * 是否已删除
     */
    @ApiModelProperty(value = "是否已删除")
      private Integer isDeleted;


}
