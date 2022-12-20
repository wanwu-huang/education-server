package org.mentpeak.test.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.mentpeak.test.entity.TestTaskDepartment;

/**
 * 测评任务部门关联表数据传输对象实体类
 *
 * @author lxp
 * @since 2022-07-12
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class TestTaskDepartmentDTO extends TestTaskDepartment {
	private static final long serialVersionUID = 1L;

}
