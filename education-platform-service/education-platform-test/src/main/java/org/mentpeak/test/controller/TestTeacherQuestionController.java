package org.mentpeak.test.controller;

import io.swagger.annotations.Api;
import lombok.AllArgsConstructor;
import org.mentpeak.core.boot.ctrl.PlatformController;
import org.mentpeak.test.service.ITestTeacherQuestionService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 教师评定题干信息表 控制器
 *
 * @author lxp
 * @since 2022-08-15
 */
@RestController
@AllArgsConstructor
@RequestMapping("/")
@Api(value = "教师评定题干信息表", tags = "教师评定题干信息表接口")
public class TestTeacherQuestionController extends PlatformController {

	private ITestTeacherQuestionService testTeacherQuestionService;



	//

}
