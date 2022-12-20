package org.mentpeak.test.controller;

import io.swagger.annotations.Api;
import lombok.AllArgsConstructor;
import org.mentpeak.core.boot.ctrl.PlatformController;
import org.mentpeak.test.service.IReportLifeStyleTaskUserService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 生活方式任务用户关联表 控制器
 *
 * @author lxp
 * @since 2022-07-12
 */
@RestController
@AllArgsConstructor
@RequestMapping("/reportlifestyletaskuser")
@Api(value = "生活方式任务用户关联表", tags = "生活方式任务用户关联表接口")
public class ReportLifeStyleTaskUserController extends PlatformController {

	private IReportLifeStyleTaskUserService reportLifeStyleTaskUserService;



}
