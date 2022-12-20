package org.mentpeak.test.controller;

import io.swagger.annotations.Api;
import lombok.AllArgsConstructor;
import org.mentpeak.core.boot.ctrl.PlatformController;
import org.mentpeak.test.service.ITestQuestionnaireDimensionService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 问卷维度关联表 控制器
 *
 * @author lxp
 * @since 2022-07-12
 */
@RestController
@AllArgsConstructor
@RequestMapping("/testquestionnairedimension")
@Api(value = "问卷维度关联表", tags = "问卷维度关联表接口")
public class TestQuestionnaireDimensionController extends PlatformController {

	private ITestQuestionnaireDimensionService testQuestionnaireDimensionService;



}
