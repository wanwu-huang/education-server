package org.mentpeak.test.vo;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.mentpeak.test.entity.TestQuestionnaireDimension;

/**
 * 问卷维度关联表视图实体类
 *
 * @author lxp
 * @since 2022-07-12
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "TestQuestionnaireDimensionVO对象", description = "问卷维度关联表")
public class TestQuestionnaireDimensionVO extends TestQuestionnaireDimension {
	private static final long serialVersionUID = 1L;

}
