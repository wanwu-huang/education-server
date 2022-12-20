package org.mentpeak.test.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.mentpeak.test.entity.TestDimensionIndexConclusion;

/**
 * 维度指标结论数据传输对象实体类
 *
 * @author lxp
 * @since 2022-07-12
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class TestDimensionIndexConclusionDTO extends TestDimensionIndexConclusion {
	private static final long serialVersionUID = 1L;

}
