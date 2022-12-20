package org.mentpeak.user.service.impl;


import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.mentpeak.common.constant.CommonConstant;
import org.mentpeak.common.enums.CaptchaEnum;
import org.mentpeak.common.util.DateUtil;
import org.mentpeak.core.auth.PlatformUser;
import org.mentpeak.core.auth.utils.SecureUtil;
import org.mentpeak.core.cloud.http.LbRestTemplate;
import org.mentpeak.core.log.exception.PlatformApiException;
import org.mentpeak.core.mybatisplus.base.BaseServiceImpl;
import org.mentpeak.core.redis.core.RedisService;
import org.mentpeak.core.tool.constant.PlatformConstant;
import org.mentpeak.core.tool.utils.*;
import org.mentpeak.user.constant.RedisPreEnum;
import org.mentpeak.user.dto.*;
import org.mentpeak.user.entity.*;
import org.mentpeak.user.exception.UserResultCode;
import org.mentpeak.user.listenter.StudentUserInfoListenter;
import org.mentpeak.user.mapper.*;
import org.mentpeak.user.props.PlatformQiNiuProperties;
import org.mentpeak.user.service.IUserService;
import org.mentpeak.user.util.StrUtil;
import org.mentpeak.user.vo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.mentpeak.core.tool.utils.RegexUtil.match;


/**
 * 服务实现类
 *
 * @author mp
 */
@Service
public class UserServiceImpl extends BaseServiceImpl<UserMapper, User> implements IUserService {
    private LbRestTemplate lbRestTemplate;

    @Autowired
    private RedisService redisService;

    @Autowired
    PlatformQiNiuProperties platformQiNiuProperties;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserExtMapper userExtMapper;

    @Autowired
    private MessageMapper messageMapper;

    @Autowired
    private DictMapper dictMapper;

    @Autowired
    private MenuDataMapper menuDataMapper;

    @Autowired
    private RoleMenuMapper roleMenuMapper;

    @Autowired
    private MenuMapper menuMapper;


    private static final Pattern pattern = Pattern.compile(CommonConstant.PASS_WORD_PATTERN);


    @Override
    public boolean submit(User user) {
        PlatformUser tempUser = SecureUtil.getUser();
        user.setTenantCode(tempUser.getTenantCode());
        if (Func.isNotEmpty(user.getPassword())) {
            user.setPassword(DigestUtil.encrypt(user.getPassword()));
        }
        Long cnt = baseMapper.selectCount(Wrappers.<User>query().lambda()
                .eq(User::getTenantCode,
                        Func.toStr(user.getTenantCode(),
                                PlatformConstant.ADMIN_TENANT_CODE))
                .eq(User::getAccount,
                        user.getAccount()));
        if (cnt > 0L) {
            throw new PlatformApiException(UserResultCode.USER_ISNOTNULL_SUCCESS);
        }
        return saveOrUpdate(user);
    }

    @Override
    public IPage<User> selectUserPage(
            IPage<User> page,
            User user) {
        return page.setRecords(baseMapper.selectUserPage(page,
                user));
    }

    @Override
    public UserInfo userInfo(
            String tenantCode,
            String account) {
        UserInfo userInfo = new UserInfo();
        // 移除租户参数
        User user = baseMapper.getUserNoTenantCode(account);
        userInfo.setUser(user);
        if (Func.isNotEmpty(user)) {
            List<String> roleAlias = baseMapper.getRoleAlias(Func.toStrArray(user.getRoleId()));
            userInfo.setRoles(roleAlias);
        }
        return userInfo;
    }

    /**
     * 用户信息by email
     *
     * @param email
     * @return
     */
    @Override
    public UserInfo userInfoByEmail(String email) {
        UserInfo userInfo = new UserInfo();
        // 移除租户参数
        User user = baseMapper.getUserByEmail(email);
        userInfo.setUser(user);
        if (Func.isNotEmpty(user)) {
            List<String> roleAlias = baseMapper.getRoleAlias(Func.toStrArray(user.getRoleId()));
            userInfo.setRoles(roleAlias);
        }
        return userInfo;
    }

    /**
     * 用户信息by account
     *
     * @param account
     * @return
     */
    @Override
    public UserInfo userInfoByAccount(String account) {
        UserInfo userInfo = new UserInfo();
        // 移除租户参数
        User user = baseMapper.getUserByAccount(account);
        userInfo.setUser(user);
        if (Func.isNotEmpty(user)) {
            List<String> roleAlias = baseMapper.getRoleAlias(Func.toStrArray(user.getRoleId()));
            userInfo.setRoles(roleAlias);
        }
        return userInfo;
    }

    @Override
    public UserInfo userInfoByPhone(
            String tenantCode,
            String phone) {
        UserInfo userInfo = new UserInfo();
        User user = baseMapper.getUserByPhone(tenantCode,
                phone);
        userInfo.setUser(user);
        if (Func.isNotEmpty(user)) {
            List<String> roleAlias = baseMapper.getRoleAlias(Func.toStrArray(user.getRoleId()));
            userInfo.setRoles(roleAlias);
        }
        return userInfo;
    }


    @Override
    public boolean grant(
            String userIds,
            String roleIds) {
        User user = new User();
        user.setRoleId(roleIds);
        return this.update(user,
                Wrappers.<User>update().lambda()
                        .in(User::getId,
                                Func.toIntList(userIds)));
    }

    @Override
    public boolean resetPassword(ResetPasswordDTO resetPasswordDTO) {
        return false;
    }

    @Override
    public List<String> getRoleName(String roleIds) {
        return baseMapper.getRoleName(Func.toStrArray(roleIds));
    }

    @Override
    public List<String> getDeptName(String deptIds) {
        return baseMapper.getDeptName(Func.toStrArray(deptIds));
    }


    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean registered(
            String authorization,
            User user,
            String code) {
        String emailRedisKey = CommonConstant.CAPTCHA_KEY + CaptchaEnum.REGISTER.getCode() + "." + user.getEmail();
        if (StringUtils.isEmpty(user.getEmail())) {
            throw new PlatformApiException(UserResultCode.EMAIL_ISNULL_ERROR);
        }
        if (StringUtils.isEmpty(code)) {
            throw new PlatformApiException(UserResultCode.CODE_ISNULL_SUCCESS);
        }
        // 先判断邮箱是否注册--->再校验验证码--->插入数据库--->获取token--->返回
        Long email = baseMapper.selectCount(new QueryWrapper<User>().eq("email",
                user.getEmail()));
        if (email > 0L) {
            throw new PlatformApiException(UserResultCode.EMAIL_REGISTED_ERROR);
        } else {
            // 验证验证码
            String smsCodeCached = Func.toStr(redisService.get(emailRedisKey));
            if (!code.equals(smsCodeCached)) {
                throw new PlatformApiException(UserResultCode.CODE_ISError_SUCCESS);
            }
            String oldPassword = user.getPassword();
            // 密码加密
            String password = DigestUtil.encrypt(oldPassword);
            user.setPassword(password);
            user.setCreateTime(LocalDateTime.now());
            //家长：6、学生：5、教师：4、学校管理员：3、教委管理员：2、超级管理员（本）：1
            user.setRoleId("3");
            baseMapper.insert(user);
            // 扩展信息
            UserExt userExt = new UserExt();
            userExt.setCreateUser(user.getId()
                    .longValue());
            userExt.setHeadUrl(CommonConstant.DEFAULT_HEAD_URL);
            userExt.setStatus(0);
            userExt.setIsDeleted(1);
            userExt.setJobId(0L);
            int insert = userExtMapper.insert(userExt);
            if (insert > 0) {
                // 注册成功
                redisService.del(emailRedisKey);
                return true;
            }
            return false;
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean forgetPassword(
            String email,
            String password,
            String code) {

        // 密码校验
        Matcher matcher = pattern.matcher(password);
        boolean presult = matcher.find();
        if (!presult) {
            throw new PlatformApiException("密码格式有误,仅支持字母以及数字，密码长度6-20之间");
        }
        Long count = baseMapper.selectCount(new QueryWrapper<User>().eq("email", email));
        if (count <= 0L) {
            throw new PlatformApiException(UserResultCode.EMAIL_NULL_ERROR);
        } else {

            // 验证验证码
            String smsRedisKey = CommonConstant.CAPTCHA_KEY + CaptchaEnum.FORGETPASSWORD.getCode() + "." + email;
            String smsCodeCached = Func.toStr(redisService.get(smsRedisKey));
            if (!code.equals(smsCodeCached)) {
                throw new PlatformApiException(UserResultCode.CODE_ISError_SUCCESS);
            }
            User user = new User();
            user.setPassword(DigestUtil.encrypt(password));
            baseMapper.update(user,
                    new QueryWrapper<User>().eq("email",
                            email));
            return true;
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public PhoneVo updatePhone(UserPhoneVo user) {
        if (StringUtils.isEmpty(user.getPhone())) {
            throw new PlatformApiException(UserResultCode.PHONE_ISNULL_SUCCESS);
        }
        if (StringUtils.isEmpty(user.getCode())) {
            throw new PlatformApiException(UserResultCode.CODE_ISNULL_SUCCESS);
        }
        // 先判断手机号是否注册--->再校验验证码--->插入数据库--->获取token--->返回
        Long phone = baseMapper.selectCount(new QueryWrapper<User>().eq("phone",
                user.getNewPhone())
                .eq("is_deleted",
                        0));
        if (phone > 0L) {
            throw new PlatformApiException(UserResultCode.PHONE_EXIST_ERROR);
        } else {
            // 验证验证码
            String smsRedisKey = RedisPreEnum.SMS_CODE_UPDATE + user.getNewPhone();
            String smsCodeCached = Func.toStr(redisService.get(smsRedisKey));
            if (!user.getCode()
                    .equals(smsCodeCached)) {
                throw new PlatformApiException(UserResultCode.CODE_ISError_SUCCESS);
            }
            // 根据redis中验证码和手机号查找对应的message数据
            Message message = messageMapper.selectOne(new QueryWrapper<Message>().eq("code",
                    smsCodeCached)
                    .eq("mobile",
                            user.getNewPhone()));
            if (StringUtil.isBlank(smsCodeCached)) {
                // 验证码过期，更新message
                message.setStatus(2);
                messageMapper.updateById(message);
                throw new PlatformApiException(UserResultCode.CODE_ISError_EXPIRED);
            }
            // 验证通过，更新message,并重置密码
            message.setStatus(1);
            messageMapper.updateById(message);
            PlatformUser uu = SecureUtil.getUser();
            User user2 = baseMapper.selectById(uu.getUserId());
            user2.setPhone(user.getNewPhone());
            baseMapper.updateById(user2);
            PhoneVo phoneVo = new PhoneVo();
            phoneVo.setAccount(user.getNewPhone());
            return phoneVo;
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean updateUserInfo(UserInfoVO userInfoVO) {
        // 将string转为localdatetime
        LocalDateTime birthday = null;
        if (!StringUtil.isEmpty(userInfoVO.getBirthday())) {
            birthday = DateUtil.parseStringToDateTime(userInfoVO.getBirthday());
        }
        User user = BeanUtil.copy(userInfoVO,
                User.class);
        user.setId(userInfoVO.getId());
        user.setBirthday(birthday);
        // 根据id修改用户信息
        int id = userMapper.updateById(user);
        if (id > 0) {
            // 根据id修改图片信息
            UserExt userExt = new UserExt();
            UserExt userExt1 = userExtMapper.selectOne(new QueryWrapper<UserExt>().eq("create_user",
                    user.getId()));
            if (ObjectUtil.isNotEmpty(userExt1)) {
                // 修改
                userExt.setHeadUrl(userInfoVO.getHeadUrl());
                userExtMapper.update(userExt,
                        new QueryWrapper<UserExt>().eq("create_user",
                                user.getId()));
            } else {
                // 插入
                userExt.setCreateUser(userInfoVO.getId());
                userExtMapper.insert(userExt);
            }
            return true;
        }
        return false;
    }

    @Override
    public List<User> teachList() {
        return userMapper.teachList();
    }

    @Override
    public boolean updatePassWord(String phone, String oldPassWord, String newPassWord) {
        Long userId = SecureUtil.getUserId();
        User user = baseMapper.selectById(userId);
        // 当前密码错误
        if (!DigestUtil.encrypt(oldPassWord).equals(user.getPassword())) {
            throw new PlatformApiException("当前密码错误");
        }
        if (!phone.equals(user.getPhone())) {
            throw new PlatformApiException("当前手机号与当前登录用户手机号不一致");
        }
        User users = new User();
        users.setId(userId);
        users.setPassword(DigestUtil.encrypt(newPassWord));
        // TODO 修改密码之后 当前用户登录状态是否 设置为未登录 【根据实际需求来定】。
        return baseMapper.updateById(users) > 0;
    }

    @Override
    public boolean updatePassWordByAccount(String account, String oldPassWord, String newPassWord) {
        Long userId = SecureUtil.getUserId();
        User user = baseMapper.selectById(userId);
        // 当前密码错误
        if (!DigestUtil.encrypt(oldPassWord).equals(user.getPassword())) {
            throw new PlatformApiException(UserResultCode.OLD_PASSWORD_IS_ERROR);
        }
        if (!account.equals(user.getAccount())) {
            throw new PlatformApiException("当前学号与当前登录用户学号不一致");
        }
        User users = new User();
        users.setId(userId);
        users.setPassword(DigestUtil.encrypt(newPassWord));
        // TODO 修改密码之后 当前用户登录状态是否 设置为未登录 【根据实际需求来定】。
        return baseMapper.updateById(users) > 0;
    }

    @Override
    public boolean updateOldPhone(String oldPhone, String phone, String codeMsg) {
        Long userId = SecureUtil.getUserId();
        User user = baseMapper.selectById(userId);
        // 说明之前绑定过手机号
        if (ObjectUtil.isNotEmpty(user.getPhone())) {
            // 比较手机号是否一致 不一致提示错误信息
            if (!user.getPhone().equals(oldPhone)) {
                throw new PlatformApiException("原手机号不一致");
            }
        }
        // 绑定手机号
        // 新绑定手机号是否存在 存在提示错误信息
        Long count = userMapper.selectCountByPhone(phone);
        if (count > 0) {
            throw new PlatformApiException(UserResultCode.PHONE_IS_BOUND);
        }
        // 验证验证码
        String smsRedisKey = RedisPreEnum.SMS_CODE_UPDATE_PHONE + phone;
        String smsCodeCached = Func.toStr(redisService.get(smsRedisKey));
        if (!codeMsg.equals(smsCodeCached)) {
            throw new PlatformApiException(UserResultCode.CODE_ISError_SUCCESS);
        }
        // 根据redis中验证码和手机号查找对应的message数据
        Message message = messageMapper.selectOne(new QueryWrapper<Message>().lambda()
                .eq(Message::getCode, smsCodeCached)
                .eq(Message::getMobile, phone));
        if (StringUtil.isBlank(smsCodeCached)) {
            // 验证码过期
            message.setStatus(2);
            messageMapper.updateById(message);
            throw new PlatformApiException(UserResultCode.CODE_ISError_EXPIRED);
        }
        // 验证通过
        message.setStatus(1);
        messageMapper.updateById(message);
        User puser = new User();
        puser.setPhone(phone);
        puser.setId(user.getId());
        baseMapper.updateById(puser);
        return true;
    }

    @Override
    public StudentInfoVO getUserInfo() {
        Long userId = SecureUtil.getUserId();
        return baseMapper.getUserInfo(userId);
    }

    @Override
    public IPage<AccountVO> accountList(IPage<AccountVO> page, AccountDTO accountDTO) {
        return baseMapper.accountList(page, accountDTO.getName(), accountDTO.getRoleId(), accountDTO.getStatus());
    }

    @Override
    public boolean resetPwd(Long userId) {
        User user = baseMapper.selectById(userId);
        user.setPassword(DigestUtil.encrypt(CommonConstant.DEFAULT_PASSWORD));
        int i = baseMapper.updateById(user);
        return i > 0 ? true : false;
    }

    @Override
    public boolean remove(Long userId) {
        int i = baseMapper.deleteById(userId);
        return i > 0 ? true : false;
    }

    @Override
    public boolean importUserInfo(MultipartFile file, Long grade) {
        try {
            // 校验文件后缀
            String filename = file.getOriginalFilename();
            String strSuffix = StrUtil.getSuffix(filename);
            if (!strSuffix.endsWith(CommonConstant.FILE_XLS) && !strSuffix.endsWith(CommonConstant.FILE_XLSX)) {
                throw new PlatformApiException("请按照用户导入模板导入测评人");
            }
            EasyExcel.read(file.getInputStream(), StudentUserInfoDTO.class,
                    new StudentUserInfoListenter(dictMapper, userMapper, userExtMapper, grade)).sheet("学生信息模版").headRowNumber(8).doRead();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public boolean addAccount(UserAccount2DTO accountDTO) {
        String name = accountDTO.getName();
        if (!IsChineseOrNum(name)) {
            throw new PlatformApiException("姓名要求中文、数字");
        }
        String account = accountDTO.getAccount();
        if (!IsEnglishOrNum(account)) {
            throw new PlatformApiException("账号要求英文字母、数字");
        }
        Long count = userMapper.selectCount(Wrappers.<User>lambdaQuery().eq(User::getAccount, account).eq(User::getIsDeleted, 0));
        if (count > 0) {
            throw new PlatformApiException("该账号已存在");
        }
        User user = new User();
        user.setRealName(name);
        user.setName(name);
        user.setAccount(account);
        user.setPassword(DigestUtil.encrypt(CommonConstant.DEFAULT_PASSWORD));
        user.setPhone(account);
        user.setRoleId(Func.toStr(accountDTO.getRoleId()));
        user.setCreateTime(LocalDateTime.now());
        user.setTenantCode(SecureUtil.getTenantCode());
        baseMapper.insert(user);
        accountDTO.getPermission().stream().forEach(permissionDTO -> {
            MenuData menuData = new MenuData();
            menuData.setUserId(user.getId());
            menuData.setRoleId(Func.toLong(accountDTO.getRoleId()));
            menuData.setMenuId(Func.toLong(permissionDTO[0]));
            if (accountDTO.getRoleId() == 2) {
                menuData.setDataId(Func.toLong(permissionDTO[1]));
            }
            menuDataMapper.insert(menuData);
        });
        // 用户扩展表
        UserExt userExt = new UserExt();
        userExt.setCreateUser(user.getId());
        userExt.setHeadUrl(CommonConstant.DEFAULT_HEAD_URL);
        userExt.setTenantCode(SecureUtil.getTenantCode());
        userExtMapper.insert(userExt);
        return true;
    }

    @Override
    public UserAccountVO getAccount(Long userId) {
        User user = baseMapper.selectById(userId);
        UserAccountVO vo = new UserAccountVO();
        vo.setName(user.getRealName());
        vo.setAccount(user.getAccount());
        vo.setUserId(userId);
        vo.setStatus(user.getStatus());
        vo.setRoleId(user.getRoleId());
        // 根据角色显示不同的权限
        List<MenuData> menuData = menuDataMapper.selectList(Wrappers.<MenuData>lambdaQuery()
                .eq(MenuData::getUserId, userId)
                .eq(MenuData::getIsDeleted, 0));
        List<String[]> per = new ArrayList<>();
        menuData.stream().forEach(menuData1 -> {
            String[] node1 = new String[1];
            String[] node2 = new String[2];
            if (menuData1.getDataId() != null) {
                node2[0] = Func.toStr(menuData1.getMenuId());
                node2[1] = Func.toStr(menuData1.getDataId());
                per.add(node2);
            } else {
                node1[0] = Func.toStr(menuData1.getMenuId());
                per.add(node1);
            }
        });
        vo.setPermission(per);
        return vo;
    }

    @Override
    public boolean updateAccount(UpdateUserAccountDTO accountDTO) {
        // 直接把原本的全删了，重新添加好了
        menuDataMapper.delete(Wrappers.<MenuData>lambdaQuery()
                .eq(MenuData::getUserId, accountDTO.getUserId()));
        User user = baseMapper.selectById(accountDTO.getUserId());
        user.setRoleId(Func.toStr(accountDTO.getRoleId()));
        user.setStatus(accountDTO.getStatus());
        baseMapper.updateById(user);

        accountDTO.getPermission().stream().forEach(permissionDTO -> {
            MenuData menuData = new MenuData();
            menuData.setUserId(accountDTO.getUserId());
            menuData.setRoleId(Func.toLong(accountDTO.getRoleId()));
            menuData.setMenuId(Func.toLong(permissionDTO[0]));
            if (accountDTO.getRoleId() == 2) {
                menuData.setDataId(Func.toLong(permissionDTO[1]));
            }
            menuDataMapper.insert(menuData);
        });
        return true;
    }

    @Override
    public List<MenuListVO> getMenuList() {
        PlatformUser user = SecureUtil.getUser();
        List<MenuData> menuData = menuDataMapper.selectList(Wrappers.<MenuData>lambdaQuery()
                .eq(MenuData::getUserId, user.getUserId())
                .eq(MenuData::getIsDeleted, 0));
        List<Long> collect = menuData.stream().map(MenuData::getMenuId).collect(Collectors.toList());

        List<MenuListVO> list = new ArrayList<>();
        // 判断用户角色
        if ("1".equals(user.getRoleId())) {
            // 超管，查询该角色开通了什么权限
            // 查询父级菜单
            List<Map<String, Object>> map = menuMapper.selectMaps(Wrappers.<Menu>query().select("id,name,path,source,alias").eq("parent_id", 0).eq("code", 1));
            map.forEach(menu -> {
                if (collect.contains(menu.get("id"))) {
                    MenuListVO vo = new MenuListVO();
                    vo.setPath(Func.toStr(menu.get("path")));
                    MetaVO metaVO = new MetaVO();
                    metaVO.setIcon(Func.toStr(menu.get("source")));
                    metaVO.setTitle(Func.toStr(menu.get("name")));
                    metaVO.setType(Func.toStr(menu.get("alias")));
                    vo.setMetaVO(metaVO);
                    // 查询子级菜单
                    List<Children2VO> menuById = menuMapper.getMenuById(Func.toLong(menu.get("id")));
                    List<MenuChildrenVO> childrenVOList = new ArrayList<>();
                    menuById.stream().forEach(childrenVO -> {
                        MenuChildrenVO cvo = new MenuChildrenVO();
                        cvo.setPath(childrenVO.getPath());
                        Meta2VO meta2VO = new Meta2VO();
                        meta2VO.setTitle(childrenVO.getTitle());
                        meta2VO.setType(childrenVO.getType());
                        cvo.setMetaVO(meta2VO);
                        childrenVOList.add(cvo);
                    });
                    vo.setChildren(childrenVOList);
                    list.add(vo);
                }
            });
        } else if ("2".equals(user.getRoleId())) {
            // 普通管理员,只展示任务、报告、预警
            List<RoleMenu> roleMenuList = roleMenuMapper.selectList(Wrappers.<RoleMenu>lambdaQuery()
                    .eq(RoleMenu::getRoleId, user.getRoleId()));
            List<Long> idList = roleMenuList.stream().map(RoleMenu::getMenuId).collect(Collectors.toList());
            List<Map<String, Object>> map2 = menuMapper.selectMaps(Wrappers.<Menu>query().select("id,name,path,source,alias").in("id", idList).eq("code", 1));
            addMenu(list, map2);
        } else if ("5".equals(user.getRoleId())) {
            // 班主任
            List<Map<String, Object>> teacherMap = menuMapper.selectMaps(Wrappers.<Menu>query().select("id,name,path,source,alias").eq("parent_id", 0).eq("code", 0));
            addMenu(list, teacherMap);
            // 是否开通了报告权限
            if (collect.contains(8L)) {
                // 将教师评定过滤
                List<Long> collect1 = collect.stream().filter(s -> s == 8).collect(Collectors.toList());
                List<Menu> menus = menuMapper.selectBatchIds(collect1);
                menus.forEach(menu -> {
                    MenuListVO vo = new MenuListVO();
                    vo.setPath(menu.getPath());
                    MetaVO metaVO = new MetaVO();
                    metaVO.setIcon(menu.getSource());
                    metaVO.setTitle(menu.getName());
                    metaVO.setType(menu.getAlias());
                    vo.setMetaVO(metaVO);
                    // 查询子级菜单
                    List<Children2VO> menuById = menuMapper.getMenuById(menu.getId());
                    List<MenuChildrenVO> childrenVOList = new ArrayList<>();
                    menuById.stream().forEach(childrenVO -> {
                        MenuChildrenVO cvo = new MenuChildrenVO();
                        cvo.setPath(childrenVO.getPath());
                        Meta2VO meta2VO = new Meta2VO();
                        meta2VO.setTitle(childrenVO.getTitle());
                        meta2VO.setType(childrenVO.getType());
                        cvo.setMetaVO(meta2VO);
                        childrenVOList.add(cvo);
                    });
                    vo.setChildren(childrenVOList);
                    list.add(vo);
                });
            }
        }

        if (list.isEmpty()) {
            throw new PlatformApiException("该账号暂无权限，请联系管理员");
        }
        return list;
    }

    @Override
    public boolean forgetPwd(ForgetPwdDTO dto) {
        Long userId = SecureUtil.getUserId();
        if (!IsEnglishOrNum(dto.getNewPassword())) {
            throw new PlatformApiException("格式要求英文、数字");
        }
        User user = baseMapper.selectById(userId);
        String password = user.getPassword();
        String oldPassword = DigestUtil.encrypt(dto.getOldPassword());
        if (!password.equals(oldPassword)) {
            throw new PlatformApiException("密码输入错误");
        }
        String newPassword = DigestUtil.encrypt(dto.getNewPassword());
        user.setPassword(newPassword);
        baseMapper.updateById(user);
        return true;
    }

    @Override
    public IsFirstVO firstLogin() {
        IsFirstVO vo = new IsFirstVO();
        // 获取登录用户角色
        PlatformUser user = SecureUtil.getUser();
        String roleId = user.getRoleId();
        if ("5".equals(roleId)) {
            // 判断是否为第一次登录
            String key = CommonConstant.TEACHER_FIRST + user.getUserId();
            Boolean hasKey = redisService.hasKey(key);
            if (hasKey) {
                // 存在
                Integer index = (Integer) redisService.get(key);
                if (index == 1) {
                    vo.setIsFirst(0);
                } else {
                    vo.setIsFirst(1);
                }
            } else {
                // 第一次登录，并存入redis
                vo.setIsFirst(1);
                redisService.set(key, 0);
            }
        } else {
            vo.setIsFirst(0);
        }
        return vo;
    }

    @Override
    public boolean submitLogin() {
        String key = CommonConstant.TEACHER_FIRST + SecureUtil.getUserId();
        redisService.set(key, 1);
        return true;
    }

    public void addMenu(List<MenuListVO> list, List<Map<String, Object>> map) {
        map.forEach(menu -> {
            MenuListVO vo = new MenuListVO();
            vo.setPath(Func.toStr(menu.get("path")));
            MetaVO metaVO = new MetaVO();
            metaVO.setIcon(Func.toStr(menu.get("source")));
            metaVO.setTitle(Func.toStr(menu.get("name")));
            metaVO.setType(Func.toStr(menu.get("alias")));
            vo.setMetaVO(metaVO);
            // 查询子级菜单
            List<Children2VO> menuById = menuMapper.getMenuById(Func.toLong(menu.get("id")));
            List<MenuChildrenVO> childrenVOList = new ArrayList<>();
            menuById.stream().forEach(childrenVO -> {
                MenuChildrenVO cvo = new MenuChildrenVO();
                cvo.setPath(childrenVO.getPath());
                Meta2VO meta2VO = new Meta2VO();
                meta2VO.setTitle(childrenVO.getTitle());
                meta2VO.setType(childrenVO.getType());
                cvo.setMetaVO(meta2VO);
                childrenVOList.add(cvo);
            });
            vo.setChildren(childrenVOList);
            list.add(vo);
        });
    }

    /**
     * 验证验证输入汉字或数字
     *
     * @param str
     * @return 如果是符合格式的字符串, 返回 <b>true </b>,否则为 <b>false </b>
     */
    public static boolean IsChineseOrNum(String str) {
        Pattern pattern = Pattern.compile("[0-9\\-\\u4e00-\\u9fa5]+");
        return pattern.matcher(str).matches();
    }

    /**
     * 验证英文或数字输入
     *
     * @param str
     * @return 如果是符合格式的字符串, 返回 <b>true </b>,否则为 <b>false </b>
     */
    public static boolean IsEnglishOrNum(String str) {
        String regex = "[A-Za-z0-9]+";
        return match(regex, str);
    }
}
