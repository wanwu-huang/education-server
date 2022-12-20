package org.mentpeak.test.controller;

import io.swagger.annotations.Api;
import lombok.AllArgsConstructor;
import org.mentpeak.core.boot.ctrl.PlatformController;
import org.mentpeak.test.service.ITestQuestionnaireModulesService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 问卷模块关联表 控制器
 *
 * @author lxp
 * @since 2022-07-12
 */
@RestController
@AllArgsConstructor
@RequestMapping("/testquestionnairemodules")
@Api(value = "问卷模块关联表", tags = "问卷模块关联表接口")
public class TestQuestionnaireModulesController extends PlatformController {

	private ITestQuestionnaireModulesService testQuestionnaireModulesService;



}
