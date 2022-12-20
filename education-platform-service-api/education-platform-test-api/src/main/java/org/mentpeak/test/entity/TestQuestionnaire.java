package org.mentpeak.test.entity;

import org.mentpeak.core.mybatisplus.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 问卷表实体类
 *
 * @author lxp
 * @since 2022-07-13
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "TestQuestionnaire对象", description = "问卷表")
public class TestQuestionnaire extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 问卷名称
     */
    @ApiModelProperty(value = "问卷名称")
      private String name;
    /**
     * 问卷名称缩写
     */
    @ApiModelProperty(value = "问卷名称缩写")
      private String abbr;
    /**
     * 封面
     */
    @ApiModelProperty(value = "封面")
      private String cover;
    /**
     * 适用范围
     */
    @ApiModelProperty(value = "适用范围")
      private String scope;
    /**
     * 问卷分类
     */
    @ApiModelProperty(value = "问卷分类")
      private Long type;
    /**
     * 简介
     */
    @ApiModelProperty(value = "简介")
      private String introduction;
    /**
     * 温馨提示
     */
    @ApiModelProperty(value = "温馨提示")
      private String reminder;
    /**
     * 排序
     */
    @ApiModelProperty(value = "排序")
      private Integer sort;
    private String route;
    /**
     * 是否有序：0无序，1 有序
     */
    @ApiModelProperty(value = "是否有序：0无序，1 有序")
      private Integer isSort;


}
