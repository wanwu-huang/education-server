package org.mentpeak.test.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.mentpeak.core.mybatisplus.base.BaseEntity;

/**
 * 问卷模块关联表实体类
 *
 * @author lxp
 * @since 2022-07-12
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "TestQuestionnaireModules对象", description = "问卷模块关联表")
public class TestQuestionnaireModules extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 测试问卷ID
     */
    @ApiModelProperty(value = "测试问卷ID")
    private Long questionnaireId;
    /**
     * 模块ID
     */
    @ApiModelProperty(value = "模块ID")
    private Long moduleId;


}
