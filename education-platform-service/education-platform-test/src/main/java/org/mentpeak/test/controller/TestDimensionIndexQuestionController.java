package org.mentpeak.test.controller;

import io.swagger.annotations.Api;
import lombok.AllArgsConstructor;
import org.mentpeak.core.boot.ctrl.PlatformController;
import org.mentpeak.test.service.ITestDimensionIndexQuestionService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 维度指标题干关联表 控制器
 *
 * @author lxp
 * @since 2022-07-12
 */
@RestController
@AllArgsConstructor
@RequestMapping("/testdimensionindexquestion")
@Api(value = "维度指标题干关联表", tags = "维度指标题干关联表接口")
public class TestDimensionIndexQuestionController extends PlatformController {

	private ITestDimensionIndexQuestionService testDimensionIndexQuestionService;



}
