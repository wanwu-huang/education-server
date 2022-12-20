package org.mentpeak.test.controller;

import io.swagger.annotations.Api;
import lombok.AllArgsConstructor;
import org.mentpeak.core.boot.ctrl.PlatformController;
import org.mentpeak.test.service.IReportFamilyStructureService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 家庭结构 控制器
 *
 * @author lxp
 * @since 2022-07-12
 */
@RestController
@AllArgsConstructor
@RequestMapping("/reportfamilystructure")
@Api(value = "家庭结构", tags = "家庭结构接口")
public class ReportFamilyStructureController extends PlatformController {

	private IReportFamilyStructureService reportFamilyStructureService;



}
