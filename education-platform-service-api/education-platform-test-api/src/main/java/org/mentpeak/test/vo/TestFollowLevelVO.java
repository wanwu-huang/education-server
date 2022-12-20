package org.mentpeak.test.vo;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.mentpeak.test.entity.TestFollowLevel;

/**
 * 关注等级
视图实体类
 *
 * @author lxp
 * @since 2022-07-12
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "TestFollowLevelVO对象", description = "关注等级")
public class TestFollowLevelVO extends TestFollowLevel {
	private static final long serialVersionUID = 1L;

}
