package org.mentpeak.test.controller;

import io.swagger.annotations.Api;
import lombok.AllArgsConstructor;
import org.mentpeak.core.boot.ctrl.PlatformController;
import org.mentpeak.test.service.IReportLifeStyleService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 生活方式（是否与父母生活） 控制器
 *
 * @author lxp
 * @since 2022-07-12
 */
@RestController
@AllArgsConstructor
@RequestMapping("/reportlifestyle")
@Api(value = "生活方式（是否与父母生活）", tags = "生活方式（是否与父母生活）接口")
public class ReportLifeStyleController extends PlatformController {

	private IReportLifeStyleService reportLifeStyleService;



}
