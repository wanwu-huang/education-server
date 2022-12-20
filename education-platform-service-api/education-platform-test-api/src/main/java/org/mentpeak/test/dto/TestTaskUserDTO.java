package org.mentpeak.test.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.mentpeak.test.entity.TestTaskUser;

/**
 * 测评任务用户关联表数据传输对象实体类
 *
 * @author lxp
 * @since 2022-07-12
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class TestTaskUserDTO extends TestTaskUser {
	private static final long serialVersionUID = 1L;

}
