package org.mentpeak.test.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.mentpeak.test.entity.TestFollowLevel;

/**
 * 关注等级
数据传输对象实体类
 *
 * @author lxp
 * @since 2022-07-12
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class TestFollowLevelDTO extends TestFollowLevel {
	private static final long serialVersionUID = 1L;

}
