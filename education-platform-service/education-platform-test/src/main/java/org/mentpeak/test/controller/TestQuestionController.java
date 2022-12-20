package org.mentpeak.test.controller;

import io.swagger.annotations.Api;
import lombok.AllArgsConstructor;
import org.mentpeak.core.boot.ctrl.PlatformController;
import org.mentpeak.test.service.ITestQuestionService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 题干信息表 控制器
 *
 * @author lxp
 * @since 2022-07-12
 */
@RestController
@AllArgsConstructor
@RequestMapping("/testquestion")
@Api(value = "题干信息表", tags = "题干信息表接口")
public class TestQuestionController extends PlatformController {

	private ITestQuestionService testQuestionService;



}
