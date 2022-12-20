package org.mentpeak.test.controller;

import io.swagger.annotations.Api;
import lombok.AllArgsConstructor;
import org.mentpeak.core.boot.ctrl.PlatformController;
import org.mentpeak.test.service.IReportTeacherUserService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 老师用户报告表 控制器
 *
 * @author lxp
 * @since 2022-07-12
 */
@RestController
@AllArgsConstructor
@RequestMapping("/reportteacheruser")
@Api(value = "老师用户报告表", tags = "老师用户报告表接口")
public class ReportTeacherUserController extends PlatformController {

	private IReportTeacherUserService reportTeacherUserService;



}
