package org.mentpeak.test.controller;

import io.swagger.annotations.Api;
import lombok.AllArgsConstructor;
import org.mentpeak.core.boot.ctrl.PlatformController;
import org.mentpeak.test.service.ITestCommonIntroductioinService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 固定文本 控制器
 *
 * @author lxp
 * @since 2022-07-12
 */
@RestController
@AllArgsConstructor
@RequestMapping("/testcommonintroductioin")
@Api(value = "固定文本", tags = "固定文本接口")
public class TestCommonIntroductioinController extends PlatformController {

	private ITestCommonIntroductioinService testCommonIntroductioinService;



}
