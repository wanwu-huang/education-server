package org.mentpeak.resourcemanager.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.mentpeak.core.mybatisplus.base.BaseEntity;

/**
 * 家长他评问卷题支信息表实体类
 *
 * @author lxp
 * @since 2022-06-17
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "ParentOption对象", description = "家长他评问卷题支信息表")
public class ParentOption extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 题干ID
     */
    @ApiModelProperty(value = "题干ID")
    private Long questionId;
    /**
     * 题支的题序
     */
    @ApiModelProperty(value = "题支的题序")
    private Integer sort;
    /**
     * 题支内容
     */
    @ApiModelProperty(value = "题支内容")
    private String title;
    /**
     * 得分
     */
    @ApiModelProperty(value = "得分")
    private Integer score;
    /**
     * 选项属性扩展
     */
    @ApiModelProperty(value = "选项属性扩展")
    private String extParam;
    /**
     * 曝光量
     */
    @ApiModelProperty(value = "曝光量")
    private Integer pv;
    /**
     * 命中次数
     */
    @ApiModelProperty(value = "命中次数")
    private Integer cpc;


}
