package org.mentpeak.test.controller;

import io.swagger.annotations.Api;
import lombok.AllArgsConstructor;
import org.mentpeak.core.boot.ctrl.PlatformController;
import org.mentpeak.test.service.ITestTeacherPaperRecordService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 教师评定问卷测试记录表 控制器
 *
 * @author lxp
 * @since 2022-08-15
 */
@RestController
@AllArgsConstructor
@RequestMapping("/")
@Api(value = "教师评定问卷测试记录表", tags = "教师评定问卷测试记录表接口")
public class TestTeacherPaperRecordController extends PlatformController {

	private ITestTeacherPaperRecordService testTeacherPaperRecordService;



}
