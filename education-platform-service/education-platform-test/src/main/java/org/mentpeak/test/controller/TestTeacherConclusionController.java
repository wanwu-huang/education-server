package org.mentpeak.test.controller;

import io.swagger.annotations.Api;
import lombok.AllArgsConstructor;
import org.mentpeak.core.boot.ctrl.PlatformController;
import org.mentpeak.test.service.ITestTeacherConclusionService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 教师他评结论 控制器
 *
 * @author lxp
 * @since 2022-07-12
 */
@RestController
@AllArgsConstructor
@RequestMapping("/testteacherconclusion")
@Api(value = "教师他评结论", tags = "教师他评结论接口")
public class TestTeacherConclusionController extends PlatformController {

	private ITestTeacherConclusionService testTeacherConclusionService;



}
