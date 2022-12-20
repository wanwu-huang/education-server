package org.mentpeak.test.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.mentpeak.core.mybatisplus.base.BaseEntity;

/**
 * 教师评定题支信息表实体类
 *
 * @author lxp
 * @since 2022-08-15
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "TestTeacherOption对象", description = "教师评定题支信息表")
public class TestTeacherOption extends BaseEntity {

    private static final long serialVersionUID = 1L;

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
