package org.mentpeak.user.service.impl;

import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.mentpeak.core.auth.utils.SecureUtil;
import org.mentpeak.core.log.exception.PlatformApiException;
import org.mentpeak.core.tool.utils.Func;
import org.mentpeak.dict.entity.Dict;
import org.mentpeak.dict.entity.ParentChild;
import org.mentpeak.dict.vo.GradeClassVO;
import org.mentpeak.dict.vo.GradeVO;
import org.mentpeak.user.dto.BindStudentDTO;
import org.mentpeak.user.entity.User;
import org.mentpeak.user.entity.UserExtAndSchool;
import org.mentpeak.user.mapper.DictMapper;
import org.mentpeak.user.mapper.ParentChildMapper;
import org.mentpeak.user.mapper.UserExtMapper;
import org.mentpeak.user.mapper.UserMapper;
import org.mentpeak.user.service.IDictService;
import org.mentpeak.user.vo.ParentStudentVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import static org.mentpeak.core.tool.utils.RegexUtil.match;

/**
 * 职业表 服务实现类
 *
 * @author lxp
 * @since 2021-03-27
 */
@Service
public class DictServiceImpl extends ServiceImpl<DictMapper, Dict> implements IDictService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserExtMapper userExtMapper;

    @Autowired
    private ParentChildMapper parentChildMapper;

    @Override
    public List<GradeVO> getGradeList() {
        List<Dict> dicts = baseMapper.selectList(Wrappers.<Dict>lambdaQuery()
                .in(Dict::getParentId, 20, 30, 40, 50)
                .eq(Dict::getIsDeleted, 0));
        List<GradeVO> list = new ArrayList<>();
        dicts.stream().forEach(dict -> {
            GradeVO vo = new GradeVO();
            vo.setId(dict.getId());
            vo.setName(dict.getDictValue());
            list.add(vo);
        });
        return list;
    }

    @Override
    public List<GradeVO> getClassList(Long id) {
        return baseMapper.getClassById(id);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean bindStudent(BindStudentDTO bindStudentDTO) {
        String studentName = bindStudentDTO.getStudentName();
        if (!IsChinese(studentName)) {
            throw new PlatformApiException("请输入正确学生姓名");
        }
        String studentNumber = bindStudentDTO.getStudentNumber();
        if (!IsNumber(studentNumber)) {
            throw new PlatformApiException("请输入正确身份证号/学籍号");
        }
        String parentsName = bindStudentDTO.getParentsName();
        if (!IsChinese(parentsName)) {
            throw new PlatformApiException("请输入正确家长姓名");
        }
        // 根据学校年级班级-学籍号查询该学生，结果添加家长-学生关联表进行绑定
        // 根据学籍号查询该学生
        User selectOne = userMapper.getUserByAccount(studentNumber);
//        User selectOne = userExtMapper.getUserByAccount(studentNumber, bindStudentDTO.getSchoolName(), bindStudentDTO.getGradeId(), bindStudentDTO.getClassId());
        if (ObjectUtils.isNotEmpty(selectOne)) {
            // 根据该学生租户编号对填写学校进行校验
            UserExtAndSchool tenantName = userExtMapper.getTenantName(selectOne.getId());
            if (!tenantName.getAddress().equals(bindStudentDTO.getSchoolName())) {
                throw new PlatformApiException("请输入正确学校名称");
            }
            if (!studentName.equals(selectOne.getRealName())) {
                throw new PlatformApiException("请输入正确学生姓名");
            }
            if (!Func.toStr(bindStudentDTO.getGradeId()).equals(tenantName.getGrade())) {
                throw new PlatformApiException("请输入正确学生年级");
            }
            if (!bindStudentDTO.getClassId().equals(tenantName.getClassId())) {
                throw new PlatformApiException("请输入正确学生班级");
            }
            // 学校赋值
            userExtMapper.updateSchool(selectOne.getId(), tenantName.getAddress());
            // 家长-学生关联
            ParentChild parentChild = new ParentChild();
            parentChild.setChildId(selectOne.getId());
            parentChild.setParentId(SecureUtil.getUserId());
            parentChildMapper.insert(parentChild);
        } else {
            throw new PlatformApiException("查无此人");
        }
        // 目前先直接进行添加学生
//        User user = new User();
//        user.setRealName(studentName);
//        // 身份证号|学籍号做账号
//        user.setAccount(studentNumber);
//        userMapper.insert(user);
//        UserExt userExt = new UserExt();
//        userExt.setCreateUser(user.getId().longValue());
//        userExt.setHeadUrl(CommonConstant.DEFAULT_HEAD_URL);
//        userExt.setAddress(bindStudentDTO.getSchoolName());
//        userExt.setGrade(String.valueOf(bindStudentDTO.getGradeId()));
//        userExt.setClassId(bindStudentDTO.getClassId());
//        userExt.setContactPerson(bindStudentDTO.getParentsName());
//        userExtMapper.insert(userExt);
//        // 家长-学生关联
//        ParentChild parentChild = new ParentChild();
//        parentChild.setChildId(user.getId());
//        parentChild.setParentId(SecureUtil.getUserId());
//        parentChildMapper.insert(parentChild);
        return true;
    }

    @Override
    public List<ParentStudentVO> bindStudentList() {
        Long userId = SecureUtil.getUserId();
        // 根据家长id,查询绑定的学生
        return baseMapper.bindStudentList(userId);
    }

    @Override
    public boolean delBindStudent(Integer studentId) {
        Long userId = SecureUtil.getUserId();
        int delete = parentChildMapper.delete(Wrappers.<ParentChild>lambdaQuery()
                .eq(ParentChild::getChildId, studentId)
                .eq(ParentChild::getParentId, userId));
        return delete > 0 ? true : false;
    }

    /**
     * 验证验证输入汉字
     *
     * @param str
     * @return 如果是符合格式的字符串, 返回 <b>true </b>,否则为 <b>false </b>
     */
    public static boolean IsChinese(String str) {
        Pattern pattern = Pattern.compile("[\\u4E00-\\u9FBF]+");
        return pattern.matcher(str).matches();
    }

    /**
     * 验证数字输入
     *
     * @param str
     * @return 如果是符合格式的字符串, 返回 <b>true </b>,否则为 <b>false </b>
     */
    public static boolean IsNumber(String str) {
        String regex = "^[0-9]*$";
        return match(regex, str);
    }

    /**
     * 验证身份证号输入
     *
     * @param str
     * @return 如果是符合格式的字符串, 返回 <b>true </b>,否则为 <b>false </b>
     */
    public static boolean IsIdCard(String str) {
        String regex = "\\d{17}[\\d|x]|\\d{15}";
        return match(regex, str);
    }


    @Override
    public List<Dict> getList(String code) {
        return baseMapper.getList(code);
    }

    @Override
    @Cacheable(cacheNames = "grade:class:info:", key = "#gradeName + ':' + #className")
    public List<GradeClassVO> listByGradeAndClassName(String gradeName, String className) {
        return baseMapper.listByGradeAndClassName(gradeName, className);
    }

    @Override
    @Cacheable(cacheNames = "grade:info:", key = "#gradeName")
    public List<Dict> listByGradeName(String gradeName) {
        return baseMapper.selectList(Wrappers.<Dict>lambdaQuery().eq(Dict::getDictValue, gradeName));
    }
}
