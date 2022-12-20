package org.mentpeak.resourcemanager.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.mentpeak.core.mybatisplus.base.BaseEntity;

/**
 * 家长他评问卷题目信息表实体类
 *
 * @author lxp
 * @since 2022-06-17
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "ParentQuestion对象", description = "家长他评问卷题目信息表")
public class ParentQuestion extends BaseEntity {

    private static final long serialVersionUID = 1L;

//    /**
//     * 主键
//     */
//    @ApiModelProperty(value = "主键")
//    @TableId(value = "id", type = IdType.AUTO)
//    private Integer id;

    /**
     * 父题干ID
     */
    @ApiModelProperty(value = "父题干ID")
    private Long parentId;
    /**
     * 问卷ID
     */
    @ApiModelProperty(value = "问卷ID")
    private Long questionnaireId;
    /**
     * 题目内容
     */
    @ApiModelProperty(value = "题目内容")
    private String title;
    /**
     * 题型
     */
    @ApiModelProperty(value = "题型")
    private Long questionType;
    /**
     * 曝光量 (题目使用次数)
     */
    @ApiModelProperty(value = "曝光量 (题目使用次数)")
    private Long pv;
    /**
     * 备注
     */
    @ApiModelProperty(value = "备注")
    private String remarks;
    /**
     * 排序
     */
    @ApiModelProperty(value = "排序")
    private Integer sort;


}
