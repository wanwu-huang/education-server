package org.mentpeak.user.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import org.mentpeak.core.http.cache.HttpCacheAble;
import org.mentpeak.core.log.annotation.ApiLog;
import org.mentpeak.core.mybatisplus.support.Condition;
import org.mentpeak.core.mybatisplus.support.Query;
import org.mentpeak.core.tool.api.Result;
import org.mentpeak.core.tool.utils.Func;
import org.mentpeak.user.dto.*;
import org.mentpeak.user.entity.User;
import org.mentpeak.user.service.IUserExtService;
import org.mentpeak.user.service.IUserService;
import org.mentpeak.user.vo.*;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;

/**
 * 控制器
 *
 * @author mp
 */
@RestController
@RequestMapping
@AllArgsConstructor
@Api(tags = "用户操作")
public class UserController {

    private IUserService userService;


    private IUserExtService userExtService;


//	/**
//	 * 用户列表
//	 */
//	@GetMapping("/list")
//	@ApiImplicitParams({
//			@ApiImplicitParam(name = "account", value = "账号名", paramType = "query", dataType = "string"),
//			@ApiImplicitParam(name = "realName", value = "姓名", paramType = "query", dataType = "string"),
//			@ApiImplicitParam(name = "phone", value = "电话", paramType = "query", dataType = "string"),
//			@ApiImplicitParam(name = "roleId", value = "角色Id", paramType = "query", dataType = "string")
//	})
//	@ApiOperation(value = "【用户信息】-列表", notes = "传入account和realName", position = 2)
//	public Result<IPage<UserVO>> list(
//			@ApiIgnore @RequestParam Map<String, Object> user,
//			Query query,
//			PlatformUser PlatformUser) {
//		return null;
//	}

    /**
     * 新增或修改
     */
    @PostMapping("/submit")
    @ApiOperation(value = "【用户信息】-用户注册", notes = "传入User", position = 3)
    @ApiLog("用户注册")
    //@PreAuth(RoleConstant.HAS_ROLE_ADMIN)
    public Result submit(
            @ApiParam(value = "邮箱号码", required = true) @RequestParam String email,
            @ApiParam(value = "密码", required = true) @RequestParam String password,
            @ApiParam(value = "验证码", required = true) @RequestParam String code,
            @ApiParam(value = "客户密钥", required = true) @RequestHeader String Authorization) {
        // TODO:验证码 注册 注册时账号，手机号不能重复，用户名
        User user = new User();
        user.setEmail(email);
        user.setName("家长" + Func.random(6));
        user.setPassword(password);
        return Result.status(userService.registered(Authorization,
                user,
                code));
    }

//	/**
//	 * 修改
//	 */
//	@PostMapping("/update")
//	@ApiOperation(value = "【用户信息】-修改", notes = "传入User", position = 3)
////	@PreAuth ( RoleConstant.HAS_ROLE_ADMIN )
//	public Result update(@Valid @RequestBody UserInfoVO userInfoVO) {
//		return Result.status(userService.updateUserInfo(userInfoVO));
//	}
//
//	/**
//	 * 删除
//	 */
//	@PostMapping("/remove")
//	@ApiOperation(value = "【用户信息】-删除", notes = "传入地基和", position = 4)
//	@PreAuth(RoleConstant.HAS_ROLE_ADMIN)
//	public Result remove(@RequestParam String userId) {
//		return Result.status(userService.deleteLogic(Func.toIntList(userId)));
//	}

//	/**
//	 * 设置菜单权限
//	 *
//	 * @param userIds
//	 * @param roleIds
//	 * @return
//	 */
//	@PostMapping("/grant")
//	@ApiOperation(value = "【用户信息】-权限设置", notes = "传入roleId集合以及menuId集合", position = 5)
//	@PreAuth(RoleConstant.HAS_ROLE_ADMIN)
//	public Result grant(
//			@ApiParam(value = "userId集合", required = true) @RequestParam String userIds,
//			@ApiParam(value = "roleId集合", required = true) @RequestParam String roleIds) {
//		boolean temp = userService.grant(userIds,
//				roleIds);
//		return Result.status(temp);
//	}
//
//	@PostMapping("/reset-password")
//	@ApiOperation(value = "【用户信息】-初始化密码", notes = "传入userId集合", position = 6)
//	@PreAuth(RoleConstant.HAS_ROLE_ADMIN)
//	public Result resetPassword(@Valid @RequestBody ResetPasswordDTO resetPasswordDTO) {
//		boolean temp = userService.resetPassword(resetPasswordDTO);
//		return Result.status(temp);
//	}

    //	/**
//	 * 用户列表
//	 *
//	 * @param user
//	 * @return
//	 */
//	@GetMapping("/user-list")
//	@ApiOperation(value = "【用户信息】-用户列表", notes = "传入user", position = 7)
//	public Result<List<User>> userList(User user) {
//		List<User> list = userService.list(Condition.getQueryWrapper(user));
//		return Result.data(list);
//	}
//
    @GetMapping("/userInfo")
    @ApiOperation(value = "【用户信息】-获取用户信息", notes = "", position = 8)
    public Result<StudentInfoVO> userInfo() {
        return Result.data(userService.getUserInfo());
    }


    @PostMapping("/forgetPassword")
    @ApiOperation(value = "【忘记密码】-忘记密码", notes = "传入User", position = 7)
//    @ApiLog("用户登录")
    public Result<?> forgetPassword(@Valid @RequestBody ForgetPasswordDTO forgetPasswordDTO) {
        return Result.status(userService.forgetPassword(forgetPasswordDTO.getEmail(), forgetPasswordDTO.getPassword(), forgetPasswordDTO.getCode()));
    }

//	@PostMapping("/updatePhone")
//	@ApiOperation(value = "【用户信息】-用户更换手机号", notes = "传入User", position = 3)
////    @ApiLog("用户更换手机号")
//	//@PreAuth(RoleConstant.HAS_ROLE_ADMIN)
//	public Result updatePhone(@Valid @RequestBody UserPhoneVo user) {
//		return Result.data(userService.updatePhone(user));
//	}
//
//	@GetMapping("/getCurrentTeacher")
//	@ApiOperation(value = "【用户信息】-根据角色获取辅导员信息", notes = "", position = 8)
//	public Result<List<TeacherVO>> getCurrentTeacher() {
//
//		List<TeacherVO> teacherVOList = new ArrayList<>();
//
//		List<User> list = new ArrayList<>();
//
//		// 用户角色是辅导员时返回本身
//		PlatformUser user = SecureUtil.getUser();
//		if (user.getRoleId()
//				.equals("2")) {
//			TeacherVO teacherVO = new TeacherVO();
//			teacherVO.setId(user.getUserId());
//			User byId = userService.getById(user.getUserId());
//			teacherVO.setRealName(byId.getName());
//			teacherVOList.add(teacherVO);
//		} else {
//			// 根据用户角色获取辅导员列表
//			list = userService.teachList();
//			list.forEach(ouser -> {
//				TeacherVO teacherVO = new TeacherVO();
//				teacherVO.setId(ouser.getId());
//				teacherVO.setRealName(ouser.getName());
//				teacherVOList.add(teacherVO);
//			});
//
//		}
//		return Result.data(teacherVOList);
//	}
//
//	@GetMapping("/getTeacher")
//	@ApiOperation(value = "【用户信息】-获取所有辅导员信息", notes = "", position = 8)
//	public Result<List<TeacherVO>> getTeacher() {
//		// 根据用户角色获取辅导员
//		List<User> list = userService.teachList();
//		// 传给前端id和辅导员名字
//		List<TeacherVO> voList = new ArrayList<>();
//		list.stream()
//				.forEach(user -> {
//					TeacherVO vo = new TeacherVO();
//					vo.setId(user.getId());
//					vo.setRealName(user.getName());
//					voList.add(vo);
//				});
//		return Result.data(voList);
//	}
//
//
//	/**
//	 * 管理员  【个人设置】-登录密码修改
//	 *
//	 * @return
//	 */
//	@PutMapping("/updatePassWord")
//	@ApiOperation(value = "【个人设置】-管理员登录密码修改", notes = "传入UpdatePhonePasswordDTO", position = 7)
//	@PreAuth(org.mentpeak.common.constant.RoleConstant.HAS_ADMIN_TEACH)
//	public Result updatePassWord(@Valid @RequestBody UpdatePhonePasswordDTO updatePhonePasswordDTO) {
//		return Result.status(userService.updatePassWord(updatePhonePasswordDTO.getPhone(),
//				updatePhonePasswordDTO.getOldPassWord(),
//				updatePhonePasswordDTO.getNewPassWord()));
//	}

    //	/**
//	 * 学员  【个人设置】-登录密码修改
//	 *
//	 * @return
//	 */
//	@PutMapping("/updatePassWordByAccount")
//	@ApiOperation(value = "【个人设置】-学员登录密码修改", notes = "传入UpdatePhonePasswordDTO", position = 7)
//	@PreAuth(org.mentpeak.common.constant.RoleConstant.HAS_STUDENT)
//	public Result updatePassWordByAccount(@Valid @RequestBody UpdatePasswordDTO updatePasswordDTO) {
//		return Result.status(userService.updatePassWordByAccount(updatePasswordDTO.getAccount(),
//				updatePasswordDTO.getOldPassWord(),
//				updatePasswordDTO.getNewPassWord()));
//	}
//
//	/**
//	 * 【个人设置】- 换绑手机号
//	 *
//	 * @param phoneDTO
//	 * @return
//	 */
//	@PutMapping("/updateOldPhone")
//	@ApiOperation(value = "【个人设置】-换绑手机号", notes = "传入UpdatePhoneDTO", position = 7)
//	public Result updateOldPhone(@Valid @RequestBody UpdatePhoneDTO phoneDTO) {
//		return Result.status(userService.updateOldPhone(phoneDTO.getOldPhone(),
//				phoneDTO.getPhone(),
//				phoneDTO.getCodeMsg()));
//	}
    @GetMapping("/accountList")
    @ApiOperation(value = "账号管理-列表")
    @ApiOperationSupport(order = 1)
    @HttpCacheAble(value = 10, maxAge = 10)
    public Result<IPage<AccountVO>> accountList(Query query, AccountDTO accountDTO) {
        return Result.data(userService.accountList(Condition.getPage(query), accountDTO));
    }

    @GetMapping("/resetPwd")
    @ApiOperation(value = "账号管理-重置密码")
    @ApiOperationSupport(order = 2)
    public Result resetPwd(@ApiParam(value = "用户id", required = true) @RequestParam Long userId) {
        return Result.status(userService.resetPwd(userId));
    }

    @GetMapping("/remove")
    @ApiOperation(value = "账号管理-删除")
    @ApiOperationSupport(order = 3)
    public Result remove(@ApiParam(value = "用户id", required = true) @RequestParam Long userId) {
        return Result.status(userService.remove(userId));
    }


    @ApiOperation(value = "导入用户信息(学生)")
    @PostMapping("/importUserInfo")
    public Result importUserInfo(@ApiParam(value = "上传的文件", required = true) @RequestPart("file") MultipartFile file,
                                 @ApiParam(value = "一级部门Id") @RequestParam(required = false) Long grade) {
        return Result.status(userService.importUserInfo(file, grade));
    }

    @PostMapping("/addAccount")
    @ApiOperation(value = "账号管理-添加账号")
    @ApiOperationSupport(order = 4)
    public Result addAccount(@Validated @RequestBody UserAccount2DTO accountDTO) {
        return Result.status(userService.addAccount(accountDTO));
    }

    @GetMapping("/getAccount")
    @ApiOperation(value = "账号管理-修改-数据回显")
    @ApiOperationSupport(order = 5)
    public Result<UserAccountVO> getAccount(@ApiParam(value = "用户id", required = true) @RequestParam Long userId) {
        return Result.data(userService.getAccount(userId));
    }

    @PostMapping("/updateAccount")
    @ApiOperation(value = "账号管理-修改")
    @ApiOperationSupport(order = 6)
    public Result updateAccount(@Validated @RequestBody UpdateUserAccountDTO accountDTO) {
        return Result.status(userService.updateAccount(accountDTO));
    }

    @ApiLog(value = "获取菜单权限")
    @GetMapping("/getMenuList")
    @ApiOperation(value = "获取菜单权限")
    @ApiOperationSupport(order = 7)
    public Result<List<MenuListVO>> getMenuList() {
        return Result.data(userService.getMenuList());
    }

    @ApiLog(value = "忘记密码")
    @PostMapping("/forgetPwd")
    @ApiOperation(value = "忘记密码")
    @ApiOperationSupport(order = 8)
    public Result forgetPwd(@Validated @RequestBody ForgetPwdDTO dto) {
        return Result.status(userService.forgetPwd(dto));
    }

    @GetMapping("/firstLogin")
    @ApiOperation(value = "教师首次登录")
    @ApiOperationSupport(order = 9)
    public Result<IsFirstVO> firstLogin() {
        return Result.data(userService.firstLogin());
    }

    @GetMapping("/submitLogin")
    @ApiOperation(value = "教师首次登录确定")
    @ApiOperationSupport(order = 10)
    public Result submitLogin() {
        return Result.status(userService.submitLogin());
    }
}
