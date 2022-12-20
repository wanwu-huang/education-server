package org.mentpeak.test.controller;

import io.swagger.annotations.Api;
import lombok.AllArgsConstructor;
import org.mentpeak.core.boot.ctrl.PlatformController;
import org.mentpeak.test.service.ITestDimensionIndexConclusionService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 维度指标结论 控制器
 *
 * @author lxp
 * @since 2022-07-12
 */
@RestController
@AllArgsConstructor
@RequestMapping("/testdimensionindexconclusion")
@Api(value = "维度指标结论", tags = "维度指标结论接口")
public class TestDimensionIndexConclusionController extends PlatformController {

	private ITestDimensionIndexConclusionService testDimensionIndexConclusionService;



}
