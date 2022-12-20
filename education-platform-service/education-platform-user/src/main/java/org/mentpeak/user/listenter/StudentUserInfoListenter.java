package org.mentpeak.user.listenter;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.mentpeak.core.auth.utils.SecureUtil;
import org.mentpeak.core.log.exception.PlatformApiException;
import org.mentpeak.core.tool.utils.DigestUtil;
import org.mentpeak.core.tool.utils.Func;
import org.mentpeak.core.tool.utils.RegexUtil;
import org.mentpeak.dict.vo.GradeClassVO;
import org.mentpeak.user.dto.StudentUserInfoDTO;
import org.mentpeak.user.entity.User;
import org.mentpeak.user.entity.UserExt;
import org.mentpeak.user.mapper.DictMapper;
import org.mentpeak.user.mapper.UserExtMapper;
import org.mentpeak.user.mapper.UserMapper;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * @describe: 解析 学员导入数据
 * @author: demain_lee
 * @since: 2022-07-18
 */
@Slf4j
public class StudentUserInfoListenter extends AnalysisEventListener<StudentUserInfoDTO> {

    private final DictMapper dictMapper;

    private final UserMapper userMapper;

    private final UserExtMapper userExtMapper;

    private final Long grade;

    public StudentUserInfoListenter(DictMapper dictMapper, UserMapper userMapper, UserExtMapper userExtMapper, Long grade) {
        this.dictMapper = dictMapper;
        this.userMapper = userMapper;
        this.userExtMapper = userExtMapper;
        this.grade = grade;
    }

    /**
     * 每隔5条存储数据库，实际使用中可以100条，然后清理list ，方便内存回收
     */
    //private static final int BATCH_COUNT = 100;

    /**
     * 缓存的数据
     */
    //private List<StudentUserInfoDTO> list = new ArrayList<>(BATCH_COUNT);

    /**
     * 记录未保存的数据
     */
    private List<StudentUserInfoDTO> unList = new ArrayList<>();

    private int count = 0;

    private int invokeCount = 0;

    @Override
    public void invokeHeadMap(Map<Integer, String> headMap, AnalysisContext context) {

        if(invokeCount == 0){
            String result = headMap.get(invokeCount);
            if(!"学生导入模版".equals(result)){
            throw new PlatformApiException("请按照用户导入模板导入测评人");
            }
        }
        if(invokeCount == 2){
            Field[] fields = StudentUserInfoDTO.class.getDeclaredFields();
            // 遍历字段进行判断
            for (Field field : fields) {
                // 获取当前字段上的ExcelProperty注解信息
                ExcelProperty fieldAnnotation = field.getAnnotation(ExcelProperty.class);
                // 判断当前字段上是否存在ExcelProperty注解
                if (fieldAnnotation != null) {
                    // 存在ExcelProperty注解则根据注解的index索引到表头中获取对应的表头名
                    String headName = headMap.get(fieldAnnotation.index());
                    // 判断表头是否为空或是否和当前字段设置的表头名不相同
                    if (Func.isEmpty(headName) || !headName.equals(fieldAnnotation.value()[0])) {
                        // 如果为空或不相同，则抛出异常不再往下执行
                        throw new PlatformApiException("请按照用户导入模板导入测评人");
                    }
                }
            }

        }
        ++invokeCount;
    }

    @Override
    public void invoke(StudentUserInfoDTO studentUserInfoDTO, AnalysisContext context) {
        log.info("解析到一条数据:{}", Func.toJson(studentUserInfoDTO));
        count++;
        saveData(studentUserInfoDTO);
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        // 排除删除模板表头情况
        if(invokeCount < 3){
            throw new PlatformApiException("请按照用户导入模板导入测评人");
        }
        log.info("所有数据解析完成！");
        unList.forEach(studentUserInfoDTO -> log.error("未保存的数据有:" + Func.toStr(studentUserInfoDTO)));
    }


    /**
     * 存储数据库
     */
    private void saveData(StudentUserInfoDTO studentUserInfoDTO) {

        // 数据校验
        if (!realNameCheck(studentUserInfoDTO.getName()) || !realNameCheck(studentUserInfoDTO.getTeacherName())) {
            throw new PlatformApiException("名字只能为中文");
        }
        if (!idCardCheck(studentUserInfoDTO.getIdCard())) {
            throw new PlatformApiException("身份证号格式有误");
        }
        if (!phoneCheck(studentUserInfoDTO.getPhone())) {
            throw new PlatformApiException("手机号格式有误");
        }
        if (!accountCheck(studentUserInfoDTO.getStudentNo())) {
            throw new PlatformApiException("学籍号格式有误");
        }

        log.info("开始存储第{}条数据到数据库", count);

        /**
         * 解析数据: 场景 年级班级数据 已提前存在字典表 年级数据库已经存在  年级班级对应关系已经存在
         */
        List<GradeClassVO> gradeClassList = new ArrayList<>();
        if (Func.isNotEmpty(grade)) {
            // 说明是列表导入
            gradeClassList = dictMapper.listByClassNameAndGradeId(grade, studentUserInfoDTO.getClassName().trim());
        } else {
            gradeClassList = dictMapper.listByGradeAndClassName(studentUserInfoDTO.getGradeName().trim(), studentUserInfoDTO.getClassName().trim());
        }
        if (gradeClassList.size() > 0) {
            // 如果存在 即 进行后续处理

            // 添加学生数据
            // 判断学生是否存在 存在即跳过
            List<User> stuList = userMapper.selectList(Wrappers.<User>lambdaQuery().eq(User::getAccount, studentUserInfoDTO.getStudentNo()).eq(User::getIsDeleted,0));

            if (stuList.size() <= 0) {

                // 该学生是否删除
                List<User> studList = userMapper.userListByAccount(studentUserInfoDTO.getStudentNo(),SecureUtil.getTenantCode());

                if(studList.size() > 0){
                    updateStudent(studentUserInfoDTO,gradeClassList,studList.get(0));
                }else{
                    addStudent(studentUserInfoDTO, gradeClassList);
                }

                // 添加老师数据
                // 判断老师是否存在 存在即不操作
                // 通过手机号来判断老师是否存在
                List<User> teachList = userMapper.selectList(Wrappers.<User>lambdaQuery().eq(User::getAccount, studentUserInfoDTO.getPhone()).or().eq(User::getPhone, studentUserInfoDTO.getPhone()));
                if (teachList.size() > 0) {
                    // 直接插入 老师班级数据
                    addTeacherClass(gradeClassList.get(0).getGradeId(),gradeClassList.get(0).getClassId(), teachList.get(0).getId());
                } else {
                    // 添加老师数据
                    addTeacher(gradeClassList.get(0).getGradeId(),gradeClassList.get(0).getClassId(), studentUserInfoDTO);
                }
            }else{
                // 学籍号重复
                unList.add(studentUserInfoDTO);
            }

        } else {
            // 说明对应关系不存在 可能年级不存  年级班级对应关系不存在
            unList.add(studentUserInfoDTO);
        }

        log.info("存储数据库成功！");
    }


    /**
     * 添加学生数据
     *
     * @param studentUserInfoDTO info
     */
    void addStudent(StudentUserInfoDTO studentUserInfoDTO, List<GradeClassVO> gradeClassList) {

        // 添加学生用户数据
        User studentUser = new User();
        studentUser.setRealName(studentUserInfoDTO.getName());
        studentUser.setAccount(studentUserInfoDTO.getStudentNo());
        studentUser.setPassword(getEncryptedPwd(getAccountPassWorld(studentUserInfoDTO.getStudentNo())));
        studentUser.setSex("男".equals(studentUserInfoDTO.getSex()) ? 0 : 1);
        // 4 学生
        studentUser.setRoleId("4");
        studentUser.setCreateUser(SecureUtil.getUserId());
        studentUser.setCreateTime(LocalDateTime.now());
        studentUser.setTenantCode(SecureUtil.getTenantCode());
        userMapper.insert(studentUser);

        GradeClassVO gradeClassVO = gradeClassList.get(0);

        // 添加学生用户拓展数据
        UserExt studentUserExt = new UserExt();
        // 拓展数据里面需要添加 年级 班级
        studentUserExt.setGrade(gradeClassVO.getGradeId().toString());
        studentUserExt.setClassId(gradeClassVO.getClassId());
        studentUserExt.setCreateUser(studentUser.getId());
        studentUserExt.setCreateTime(LocalDateTime.now());
        studentUserExt.setTenantCode(SecureUtil.getTenantCode());
        userExtMapper.insert(studentUserExt);

        // 学生班级 关联表需要添加数据(废弃)
        // addStuClass(gradeClassList, studentUser.getId(), studentUser.getTenantCode());
    }


    /**
     * 用户更新操作 （针对删除用户）
     * @param studentUserInfoDTO
     * @param gradeClassList
     */
    void updateStudent(StudentUserInfoDTO studentUserInfoDTO, List<GradeClassVO> gradeClassList,User user) {

        // 更新学生用户数据
        user.setRealName(studentUserInfoDTO.getName());
        user.setSex("男".equals(studentUserInfoDTO.getSex()) ? 0 : 1);
        // 4 学生
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateUser(SecureUtil.getUserId());
        user.setUpdateTime(LocalDateTime.now());
        user.setIsDeleted(0);
        userMapper.updateUser(user.getId(),user.getRealName(),user.getSex(),user.getUpdateUser(),user.getUpdateTime());

        GradeClassVO gradeClassVO = gradeClassList.get(0);

        // 更新学生用户拓展数据
        List<UserExt> userExtsList = userExtMapper.userExtList(user.getId());
        if(userExtsList.size() > 0){
            UserExt studentUserExt =  userExtsList.get(0);
            // 拓展数据里面需要添加 年级 班级
            studentUserExt.setGrade(gradeClassVO.getGradeId().toString());
            studentUserExt.setClassId(gradeClassVO.getClassId());
            studentUserExt.setUpdateTime(LocalDateTime.now());
            userExtMapper.updateUserExt(studentUserExt.getId(),studentUserExt.getGrade(),studentUserExt.getClassId(),studentUserExt.getUpdateTime());
        }
    }

    /**
     * 添加班主任老师数据
     *
     * @param studentUserInfoDTO info
     */
    void addTeacher(Long gradeId,Long classId, StudentUserInfoDTO studentUserInfoDTO) {

        // 根据手机号判断教师是否是已删除
        User user = userMapper.userByPhone(studentUserInfoDTO.getPhone(), SecureUtil.getTenantCode());
        if(Func.isNotEmpty(user)){
            // 说明之前删除过数据
            userMapper.updateTeacherUser(user.getId(),SecureUtil.getUserId(),LocalDateTime.now());
            // 移除老师班级关联表数据
            removeTeacherClass(user.getId());
            // 添加老师班级数据
            addTeacherClass(gradeId,classId, user.getId());
        }else {

            // 添加老师用户数据
            User teacherUser = new User();
            teacherUser.setTenantCode(SecureUtil.getTenantCode());
            teacherUser.setRealName(studentUserInfoDTO.getTeacherName());
            teacherUser.setPhone(studentUserInfoDTO.getPhone());
            teacherUser.setAccount(studentUserInfoDTO.getPhone());
            teacherUser.setPassword(getEncryptedPwd(getPhonePassWorld(studentUserInfoDTO.getIdCard())));
            // 5 班主任
            teacherUser.setRoleId("5");
            teacherUser.setCreateUser(SecureUtil.getUserId());
            teacherUser.setCreateTime(LocalDateTime.now());
            userMapper.insert(teacherUser);
            // 添加老师用户拓展数据
            UserExt teacherUserExt = new UserExt();
            teacherUserExt.setTenantCode(SecureUtil.getTenantCode());
            teacherUserExt.setCreateUser(teacherUser.getId());
            teacherUserExt.setIdCard(studentUserInfoDTO.getIdCard());
            teacherUserExt.setCreateTime(LocalDateTime.now());
            userExtMapper.insert(teacherUserExt);

            // 老师班级数据
            addTeacherClass(gradeId,classId, teacherUser.getId());
        }

    }


    /**
     * 添加班级老师数据
     */
    void addTeacherClass(Long gradeId,Long classId, Long teacherId) {
        // 老师 班级 存在既不插入
        Integer count = userMapper.teacherClassCount(gradeId,classId, teacherId);
        if (count == 0) {
            // 插入老师班级数据
            userMapper.saveTeachClass(SecureUtil.getTenantCode(),gradeId, classId, teacherId);
        }

    }

    /**
     * 移除班级老师数据
     * @param teacherId
     */
    void removeTeacherClass(Long teacherId) {
        // 老师 班级 存在既不插入
        userMapper.deleteTeacherClass(teacherId);
    }


    /**
     * 添加学生班级数据
     *
     * @param gradeClassList 年级班级
     * @param stuId          学生ID
     * @param tenantCode     租户
     */
    void addStuClass(List<GradeClassVO> gradeClassList, Long stuId, String tenantCode) {
        // 添加学生班级数据
        GradeClassVO gradeClassVO = gradeClassList.get(0);
        userMapper.saveUserClass(tenantCode, gradeClassVO.getClassId(), stuId);
    }


    /**
     * 密码 账号后 6位 + 123
     *
     * @param account 账号
     * @return 密码
     */
    String getAccountPassWorld(String account) {
        return account.substring(account.length() - 6) + "123";
    }

    /**
     * 密码 账号后 6位
     *
     * @param account 账号
     * @return 密码
     */
    String getPhonePassWorld(String account) {
        return account.substring(account.length() - 6);
    }

    /**
     * 加密密码
     *
     * @param pwd 密码
     * @return 加密后密码
     */
    String getEncryptedPwd(String pwd) {
        return DigestUtil.encrypt(pwd);
    }

    /**
     * 姓名校验
     * 匹配中文字符
     *
     * @param str
     * @return
     */
    public static boolean realNameCheck(String str) {
        String regEx = "^[\\u4e00-\\u9fa5]{0,}$";
        return RegexUtil.match(regEx,str);
    }

    /**
     * 身份证校验
     * 验证身份证号（15位或18位数字）
     *
     * @param str
     * @return
     */
    public static boolean idCardCheck(String str) {
        String regEx = "^\\d{15}|\\d{18}$";
        return RegexUtil.match(regEx,str);
    }


    /**
     * 手机号校验
     *
     * @param str
     * @return
     */
    public static boolean phoneCheck(String str) {
        String regEx = "^1[3456789][0-9]{9}";
        return RegexUtil.match(regEx,str);
    }

    /**
     * 学籍号校验
     *
     * @param str
     * @return
     */
    public static boolean accountCheck(String str) {
        String regEx = "^[0-9]*$";
        return RegexUtil.match(regEx,str);
    }
}
