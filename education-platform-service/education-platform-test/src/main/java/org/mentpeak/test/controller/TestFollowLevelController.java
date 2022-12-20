package org.mentpeak.test.controller;

import io.swagger.annotations.Api;
import lombok.AllArgsConstructor;
import org.mentpeak.core.boot.ctrl.PlatformController;
import org.mentpeak.test.service.ITestFollowLevelService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 关注等级
 控制器
 *
 * @author lxp
 * @since 2022-07-12
 */
@RestController
@AllArgsConstructor
@RequestMapping("/testfollowlevel")
@Api(value = "关注等级", tags = "关注等级接口")
public class TestFollowLevelController extends PlatformController {

	private ITestFollowLevelService testFollowLevelService;



}
