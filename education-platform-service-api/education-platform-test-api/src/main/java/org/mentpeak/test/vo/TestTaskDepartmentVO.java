package org.mentpeak.test.vo;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.mentpeak.test.entity.TestTaskDepartment;

/**
 * 测评任务部门关联表视图实体类
 *
 * @author lxp
 * @since 2022-07-12
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "TestTaskDepartmentVO对象", description = "测评任务部门关联表")
public class TestTaskDepartmentVO extends TestTaskDepartment {
	private static final long serialVersionUID = 1L;

}
