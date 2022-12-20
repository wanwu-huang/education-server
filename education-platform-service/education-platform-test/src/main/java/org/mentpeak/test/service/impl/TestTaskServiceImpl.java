package org.mentpeak.test.service.impl;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.metadata.WriteTable;
import com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.mentpeak.common.util.TimeUtil;
import org.mentpeak.common.util.UUIDUtil;
import org.mentpeak.core.auth.PlatformUser;
import org.mentpeak.core.auth.utils.SecureUtil;
import org.mentpeak.core.log.exception.PlatformApiException;
import org.mentpeak.core.mybatisplus.base.BaseServiceImpl;
import org.mentpeak.core.tool.api.Result;
import org.mentpeak.core.tool.utils.Func;
import org.mentpeak.test.dto.*;
import org.mentpeak.test.entity.*;
import org.mentpeak.test.listener.ExportListener;
import org.mentpeak.test.mapper.*;
import org.mentpeak.test.service.ITestTaskService;
import org.mentpeak.test.vo.*;
import org.mentpeak.test.wrapper.TestTaskWrapper;
import org.mentpeak.user.entity.MenuData;
import org.mentpeak.user.entity.UserExt;
import org.mentpeak.user.feign.IUserClient;
import org.springframework.stereotype.Service;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * 测评任务表 服务实现类
 *
 * @author lxp
 * @since 2022-07-12
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TestTaskServiceImpl extends BaseServiceImpl<TestTaskMapper, TestTask> implements ITestTaskService {

    private final TestTaskDepartmentMapper testTaskDepartmentMapper;

    private final TestTaskQuestionnaireMapper testTaskQuestionnaireMapper;

    private final TestApproachMapper testApproachMapper;

    private final IUserClient userClient;

    private final TestTaskUserMapper taskUserMapper;
    private final TestQuestionMapper testQuestionMapper;
    private final TestOptionMapper testOptionMapper;

    private final MenuDataMapper menuDataMapper;

    private final TestPaperRecordMapper paperRecordMapper;


    private static final int PAGE_SIZE = 100;

    /**
     * 下载路径
     */
    private final static String DOWNLOAD_DIR = "download";
    /**
     * 下载压缩包路径
     */
    private final static String DOWNLOAD_ZIP = "downloadZip";

    @Override
    public IPage<TestTaskVO> selectTestTaskPage(IPage<TestTaskVO> page, TestTaskVO testTask) {
        return page.setRecords(baseMapper.selectTestTaskPage(page, testTask));
    }


    @Override
    public TestApproach addTestTask(TestTaskDTO testTaskDTO) {

        // 添加测评任务
        TestTask testTask = new TestTask();
        testTask.setTestApproachId(testTaskDTO.getApproachId());
        testTask.setTaskName(testTaskDTO.getTaskName());
        testTask.setBeginTime(testTaskDTO.getBeginTime());
        testTask.setEndTime(testTaskDTO.getEndTime());
        testTask.setTenantCode(SecureUtil.getTenantCode());
        // 测试途径
        testTask.setReportIsVisible(testTaskDTO.getReportIsVisible());
        this.baseMapper.insert(testTask);

        // 添加 任务 部门（年级关联）
        Long[] gradeId = testTaskDTO.getGradeId();
        Arrays.stream(gradeId).forEach(grade -> {
            TestTaskDepartment testTaskDepartment = new TestTaskDepartment();
            testTaskDepartment.setTestTaskId(testTask.getId());
            testTaskDepartment.setTestDepartmentId(grade);
            testTaskDepartmentMapper.insert(testTaskDepartment);
        });

        // 添加 任务 问卷关联表
        Long[] questionnaireId = testTaskDTO.getQuestionnaireId();
        Arrays.stream(questionnaireId).forEach(questionnaire -> {
            TestTaskQuestionnaire testTaskQuestionnaire = new TestTaskQuestionnaire();
            testTaskQuestionnaire.setTestTaskId(testTask.getId());
            testTaskQuestionnaire.setQuestionnaireId(questionnaire);
            testTaskQuestionnaireMapper.insert(testTaskQuestionnaire);
        });

        return getTestApproach(testTaskDTO.getApproachId());
    }


    /**
     * 获取测评途径信息
     *
     * @param approachId id
     * @return TestApproach
     */
    TestApproach getTestApproach(Long approachId) {
        return testApproachMapper.selectById(approachId);
    }

    @Override
    public List<TaskListVO> getList() {
        Long userId = SecureUtil.getUserId();
        UserExt data = null;
        List<TaskListVO> voList = new ArrayList<>();
        try {
            Result<UserExt> result = userClient.userExtInfoById(userId);
            data = result.getData();
        } catch (Exception e) {
            log.info("查不到该用户信息");
            e.printStackTrace();
        }
        if (ObjectUtils.isNotEmpty(data)) {
            List<TestTaskDepartment> testTaskDepartments = testTaskDepartmentMapper.selectList(Wrappers.<TestTaskDepartment>lambdaQuery()
                    .eq(TestTaskDepartment::getTestDepartmentId, data.getGrade()));
            // 全部任务id
            if (ObjectUtils.isNotEmpty(testTaskDepartments)) {
                List<Long> idList = testTaskDepartments.stream().map(TestTaskDepartment::getTestTaskId).collect(Collectors.toList());
                List<TestTask> testTaskList = baseMapper.selectBatchIds(idList);
                LocalDate nowTime = LocalDate.now();
                testTaskList.stream().forEach(testTask -> {
                    TaskListVO vo = new TaskListVO();
                    String beginTime = testTask.getBeginTime();
                    String endTime = testTask.getEndTime();
                    vo.setId(testTask.getId());
                    vo.setTaskName(testTask.getTaskName());
                    vo.setBeginTime(beginTime);
                    vo.setEndTime(endTime);
                    // 默认未完成
                    vo.setStatus(0);
                    // 判断该用户是否完成该任务
                    TestTaskUser testTaskUser = taskUserMapper.selectOne(Wrappers.<TestTaskUser>lambdaQuery()
                            .eq(TestTaskUser::getTestTaskId, testTask.getId())
                            .eq(TestTaskUser::getUserId, userId)
                            .orderByDesc(TestTaskUser::getCreateTime)
                            .last("limit 1"));
                    if (ObjectUtils.isNotEmpty(testTaskUser)) {
                        vo.setStatus(testTaskUser.getCompletionStatus());
                    }
                    // 判断任务时间是否过期
                    if (vo.getStatus() == 0) {
                        // 当前时间在截至时间后面，为已完成
                        if (nowTime.isAfter(parseDate(endTime))) {
                            vo.setStatus(1);
                        } else if (nowTime.isBefore(parseDate(beginTime))) {
                            vo.setStatus(2);
                        } else {
                            vo.setStatus(0);
                        }
                    }
                    voList.add(vo);
                });
            }
        }
        return voList;
    }

    private LocalDate parseDate(String dateStr) {
        DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate parse = LocalDate.parse(dateStr, DATE_FORMAT);
        // 当天不算过期，故判定时间往后延迟一天
        return parse;
    }


    @Override
    public Page<TaskInfoVO> testTaskList(IPage<TestRecordVO> page, TaskSearchDTO taskSearchDTO) {
        PlatformUser user = SecureUtil.getUser();
        String tenantCode = SecureUtil.getTenantCode();
        List<Long> dataIdList = new ArrayList<>();
        Page<TaskInfoVO> taskInfoVoPage = new Page<>();
        if ("2".equals(user.getRoleId())) {
            // 查找开通权限的任务id
            if(taskSearchDTO.getInterfaceType() == 0){
                dataIdList = menuDataMapper.selectList(Wrappers.<MenuData>lambdaQuery()
                    .eq(MenuData::getUserId, user.getUserId())
                    .eq(MenuData::getMenuId, 1)
                    .eq(MenuData::getIsDeleted, 0)).stream().map(MenuData::getDataId).collect(
                    Collectors.toList());
            }

            if(taskSearchDTO.getInterfaceType() == 1){
                dataIdList = menuDataMapper.selectList(Wrappers.<MenuData>lambdaQuery()
                    .eq(MenuData::getUserId, user.getUserId())
                    .eq(MenuData::getMenuId, 8)
                    .eq(MenuData::getIsDeleted, 0)).stream().map(MenuData::getDataId).collect(
                    Collectors.toList());
            }

            if(dataIdList.size() == 0){
                return taskInfoVoPage;
            }
        }

        if ("5".equals(user.getRoleId())) {
            // 查找开通权限的任务id
            if(taskSearchDTO.getInterfaceType() == 1){
                dataIdList = menuDataMapper.selectList(Wrappers.<MenuData>lambdaQuery()
                    .eq(MenuData::getUserId, user.getUserId())
                    .eq(MenuData::getMenuId, 8)
                    .eq(MenuData::getIsDeleted, 0)).stream().map(MenuData::getDataId).collect(
                    Collectors.toList());
            }

            if(dataIdList.size() == 0){
                return taskInfoVoPage;
            }
        }

        taskInfoVoPage = baseMapper.getTestTaskList(page, taskSearchDTO.getTaskName(), taskSearchDTO.getTaskCreateTime(), taskSearchDTO.getBeginTime(), taskSearchDTO.getEndTime(), taskSearchDTO.getTaskStatus(), dataIdList,tenantCode);
        List<TaskInfoVO> taskInfoVOList = taskInfoVoPage.getRecords();

        taskInfoVOList.forEach(task -> {
            Integer totalTestCount = baseMapper.totalTestCount(task.getId());
            Long count = paperRecordMapper.selectCount(
                    Wrappers.<TestPaperRecord>lambdaQuery().eq(TestPaperRecord::getTaskId, task.getId())
                            .isNotNull(TestPaperRecord::getReportId));
            Integer isReport = 0;
            if (count > 0) {
                isReport = 1;
            }
            task.setIsReport(isReport);
            task.setTestPeopleCount(totalTestCount);
            task.setTaskStatusName(task.getTaskStatus().equals(0) ? "未完成" : "已完成");
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            TemporalAccessor temporalAccessor = dateTimeFormatter.parse(task.getCreateTime());
            String createTime = dateFormatter.format(temporalAccessor);
            task.setCreateTime(createTime);
        });
        return taskInfoVoPage;
    }

    @Override
    public TestTaskVO taskInfoById(Long taskId) {
        TestTask testTask = baseMapper.selectById(taskId);
        TestTaskWrapper testTaskWrapper = new TestTaskWrapper();
        TestTaskVO testTaskVO = testTaskWrapper.entityVO(testTask);
        TestApproach testApproach = testApproachMapper.selectById(testTask.getTestApproachId());
        testTaskVO.setQrCode(testApproach.getQrCode());
        testTaskVO.setType(testApproach.getId());
        return testTaskVO;
    }

    @Override
    public Page<TaskDetailVO> taskDetailListById(IPage<TestRecordVO> page, TaskDetailSearchDTO taskDetaiSearchDTO) {
        Page<TaskDetailVO> taskDetailVoPage = baseMapper.taskDetailListById(page, taskDetaiSearchDTO.getTestTaskId(), taskDetaiSearchDTO.getName(), taskDetaiSearchDTO.getAccount(),
                taskDetaiSearchDTO.getSex(), taskDetaiSearchDTO.getBeginTime(), taskDetaiSearchDTO.getEndTime(), taskDetaiSearchDTO.getTestDepartmentId(), taskDetaiSearchDTO.getClassId(), taskDetaiSearchDTO.getCompletionStatus());
        List<TaskDetailVO> detailVOList = taskDetailVoPage.getRecords();
        detailVOList.forEach(taskDetail -> {
                    if (ObjectUtils.isNotEmpty(taskDetail.getSex())) {
                        taskDetail.setSexName(taskDetail.getSex() == 0 ? "男" : "女");
                    }
                }
        );
        return taskDetailVoPage;
    }

    @Override
    public boolean updateTestTaskById(TaskUpdateDTO taskUpdateDTO) {
        TestTask testTask = new TestTask();
        testTask.setId(taskUpdateDTO.getTestTaskId());
        testTask.setBeginTime(taskUpdateDTO.getBeginTime());
        testTask.setEndTime(taskUpdateDTO.getEndTime());
        return baseMapper.updateById(testTask) > 0;
    }

    @Override
    public void exportNoCompletion(TaskDetailSearchDTO taskDetailSearchDTO, HttpServletResponse response) {
        // 查询所有未完成
        List<TaskDetailVO> taskDetailVOList = baseMapper.taskDetailListByIdAndCompletionStatus(taskDetailSearchDTO.getTestTaskId(), taskDetailSearchDTO.getCompletionStatus());
        TestTask testTask = baseMapper.selectById(taskDetailSearchDTO.getTestTaskId());
        taskDetailVOList.stream().filter(taskDetailVO -> Func.isNotEmpty(taskDetailVO.getSex())).forEach(taskDetail -> taskDetail.setSexName(taskDetail.getSex() == 0 ? "男" : "女"));
        response.setContentType("application/vnd.ms-excel");
        response.setCharacterEncoding("utf-8");
        try {
            String fileSuffix = taskDetailSearchDTO.getCompletionStatus() == 0 ? "未完成情况导出" : "已完成情况导出" ;
            String fileName = URLEncoder.encode(testTask.getTaskName() + fileSuffix, "UTF-8");

            response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");

            EasyExcel.write(response.getOutputStream(),
                    TaskDetailVO.class)
                    .sheet("sheet0")
                    // 设置字段宽度为自动调整，不太精确
                    .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
                    .doWrite(taskDetailVOList);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public boolean deleteTask(Long testTaskId) {

        /**
         * 如果该任务下，有提交完成的测评数据，不能删除任务，点击【删除】，提示：已有测评数据，不能删除该任务
         */
        List<TestTaskUser> testTaskUserList = taskUserMapper.selectList(Wrappers.<TestTaskUser>lambdaQuery().eq(TestTaskUser::getTestTaskId, testTaskId));

        if (testTaskUserList.size() > 0) {
            throw new PlatformApiException("已有测评数据，不能删除该任务");
        }

        // 删除 测评任务
        this.baseMapper.deleteById(testTaskId);

        // 删除 任务 部门（年级关联）
        testTaskDepartmentMapper.delete(Wrappers.<TestTaskDepartment>lambdaQuery().eq(TestTaskDepartment::getTestTaskId, testTaskId));

        // 删除 任务 问卷关联表
        testTaskQuestionnaireMapper.delete(Wrappers.<TestTaskQuestionnaire>lambdaQuery().eq(TestTaskQuestionnaire::getTestTaskId, testTaskId));

        return true;
    }


    @Override
    public void exportFinisherScore(TaskDetailSearchDTO taskDetailSearchDTO, HttpServletRequest request, HttpServletResponse response) {

        long startTime = System.currentTimeMillis();
        int startIndex = 1;

        List<TaskUserInfoVO> taskUserList = new ArrayList<>();

        while (true) {
            int startParam = (startIndex - 1) * PAGE_SIZE;
            int pageIndex = (int) Math.ceil((double) startParam / (double) PAGE_SIZE + 1);
            IPage<TaskUserInfoVO> pageQuery = new Page<>(pageIndex, PAGE_SIZE, false);
            Page<TaskUserInfoVO> taskUserInfoVOPage = baseMapper.taskUserInfoList(pageQuery, taskDetailSearchDTO.getTestTaskId());

            //完成任务用户信息
            List<TaskUserInfoVO> userInfoVOList = taskUserInfoVOPage.getRecords();
            if (CollectionUtils.isEmpty(userInfoVOList)) {
                break;
            }
            taskUserList.addAll(userInfoVOList);
            startIndex++;
        }

        TestTask testTask = baseMapper.selectById(taskDetailSearchDTO.getTestTaskId());
        String taskName = testTask.getTaskName();
        String uuid = UUIDUtil.shortUuid();
        File fileMkDir = new File(DOWNLOAD_DIR + File.separator + uuid);
        if (!fileMkDir.exists()) {
            fileMkDir.mkdirs();
        }

        String filePath = DOWNLOAD_DIR + File.separator + uuid + File.separator + taskName + "批量导出原始分" + ".xlsx";
        File file = new File(filePath);
        ExcelWriter excelWriter = EasyExcel.write(file).build();


        WriteTable table = new WriteTable();
        table.setTableNo(1);
        table.setHead(head(testQuestionMapper));

        WriteSheet sheet1 = new WriteSheet();
        sheet1.setSheetName("原始分");
        sheet1.setSheetNo(0);

        excelWriter.write(contentData(taskUserList, testOptionMapper), sheet1, table);

        // 关闭流
        if (excelWriter != null) {
            excelWriter.finish();
        }

        // 下载
        try {
            downloadZip(request, response, taskName, uuid);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        //删除暂存目录
        try {
            if (null != fileMkDir) {
                FileUtils.deleteDirectory(fileMkDir);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        long endTime = System.currentTimeMillis();
        log.info("导出耗时:{}", endTime - startTime);

    }

    @Override
    public void exportScore(TaskDetailExportDTO taskDetailExportDTO, HttpServletRequest request, HttpServletResponse response) {

        ServletOutputStream outputStream = null;
        try {
            outputStream = ExportListener.getServletOutputStream(response, "导出原始分");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        ExcelWriter excelWriter = EasyExcel.write(outputStream).build();

        WriteTable table = new WriteTable();
        table.setTableNo(1);
        table.setHead(head(testQuestionMapper));

        WriteSheet sheet1 = new WriteSheet();
        sheet1.setSheetName("原始分");
        sheet1.setSheetNo(0);


        List<TaskUserInfoVO> taskUserInfoVOList = baseMapper.taskUserInfo(taskDetailExportDTO.getTestTaskId(), taskDetailExportDTO.getUserId());
        if (taskUserInfoVOList.size() == 0) {
            // 没有做题数据
            TaskUserInfoVO taskUserInfoVO = baseMapper.userInfoById(taskDetailExportDTO.getUserId());
            excelWriter.write(userContentData(taskUserInfoVO, testOptionMapper), sheet1, table);
        } else {
            excelWriter.write(contentData(taskUserInfoVOList, testOptionMapper), sheet1, table);
        }

        // 关闭流
        if (excelWriter != null) {
            excelWriter.finish();
        }
    }


    /**
     * head
     *
     * @return
     */
    private static List<List<String>> head(TestQuestionMapper testQuestionMapper) {
        List<List<String>> headTitles = Lists.newArrayList();
        String SortInfo = "序号", basicInfo = "基本信息", timeInfo = "答题时间", qName = "心里测评问卷";
        //第一列，1/2行
        headTitles.add(Lists.newArrayList(SortInfo, SortInfo));
        //第二列，1/2行
        headTitles.add(Lists.newArrayList(basicInfo, "姓名"));
        headTitles.add(Lists.newArrayList(basicInfo, "学籍号"));
        headTitles.add(Lists.newArrayList(basicInfo, "性别"));
        headTitles.add(Lists.newArrayList(basicInfo, "一级部门"));
        headTitles.add(Lists.newArrayList(basicInfo, "二级部门"));
        headTitles.add(Lists.newArrayList(timeInfo, "总答题时间"));
        headTitles.add(Lists.newArrayList(timeInfo, "开始时间"));
        headTitles.add(Lists.newArrayList(timeInfo, "结束时间"));

        List<Integer> qIds = testQuestionMapper.selectList(Wrappers.<TestQuestion>lambdaQuery().orderByAsc(TestQuestion::getSort)).stream().map(TestQuestion::getSort).collect(Collectors.toList());

        qIds.forEach(q -> {
            headTitles.add(Lists.newArrayList(qName, q.toString()));
        });

        return headTitles;
    }

    /**
     * 数据
     *
     * @param taskUserList
     * @param testOptionMapper
     * @return
     */
    private static List<List<String>> contentData(List<TaskUserInfoVO> taskUserList, TestOptionMapper testOptionMapper) {

        List<String> totalList = new LinkedList<>();

        List<List<String>> contentList = Lists.newArrayList();

        AtomicReference<Integer> sort = new AtomicReference<>(0);

        taskUserList.forEach(info -> {
            sort.getAndSet(sort.get() + 1);
            totalList.add(sort.get().toString());
            totalList.add(info.getRealName());
            totalList.add(info.getAccount());
            totalList.add(getSex(info.getSex()));
            totalList.add(info.getGradeName());
            totalList.add(info.getClassName());
            totalList.add(getTotalTime(info.getStartTime(), info.getFinishTime()));
            totalList.add(info.getStartTime());
            totalList.add(info.getFinishTime());
            List<String> scoreList = testOptionMapper.optionScoreList(info.getPaperId());
            totalList.addAll(scoreList);
            contentList.add(totalList);

        });

        return contentList;

    }

    private static List<List<String>> userContentData(TaskUserInfoVO info, TestOptionMapper testOptionMapper) {

        List<String> totalList = new LinkedList<>();
        List<List<String>> contentList = Lists.newArrayList();

        if (Func.isNotEmpty(info)) {
            totalList.add("1");
            totalList.add(info.getRealName());
            totalList.add(info.getAccount());
            totalList.add(getSex(info.getSex()));
            totalList.add(info.getGradeName());
            totalList.add(info.getClassName());
            contentList.add(totalList);
        }

        return contentList;

    }

    /**
     * 下载压缩包
     */
    public void downloadZip(HttpServletRequest request, HttpServletResponse response, String taskName, String uuid) throws IOException {
        OutputStream out = null;
        File zip = null;

        File fileDir = new File(DOWNLOAD_ZIP + File.separator + uuid);
        //如果文件夹不存在
        if (!fileDir.exists()) {
            //创建文件夹
            fileDir.mkdirs();
        }

        //多个文件进行压缩，批量打包下载文件
        //创建压缩文件需要的空的zip包
        String zipName = taskName + "批量导出原始分".concat(".zip");
        String zipFilePath = DOWNLOAD_ZIP + File.separator + uuid + File.separator + zipName;
        //压缩文件
        zip = new File(zipFilePath);
        //创建文件，存在覆盖
        zip.createNewFile();

        //创建zip文件输出流
        ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zip));
        this.zipFile(DOWNLOAD_DIR + File.separator + uuid, zos);
        zos.close();

        //将打包后的文件写到客户端，输出的方法同上，使用缓冲流输出
        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(zipFilePath));
        byte[] buff = new byte[bis.available()];
        bis.read(buff);
        bis.close();

        //IO流实现下载的功能
        //设置编码字符
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/zip");
        //防止文件名乱码
        String userAgent = request.getHeader("USER-AGENT");

        //火狐浏览器
        if (userAgent.contains("Firefox") || userAgent.contains("firefox")) {
            zipName = new String(zipName.getBytes(), "ISO8859-1");
        } else {
            //其他浏览器
            zipName = URLEncoder.encode(zipName, "UTF-8");
        }
        //设置下载的压缩文件名称
        response.setHeader("Content-Disposition", "attachment;filename*=utf-8''" + zipName);
        //创建页面返回方式为输出流，会自动弹出下载框
        out = response.getOutputStream();
        //输出数据文件
        out.write(buff);

        //下载完后删除文件夹和压缩包
        FileUtils.deleteDirectory(fileDir);
        zip.delete();

        //释放缓存
        out.flush();
        //关闭输出流
        out.close();
    }


    /**
     * 压缩文件
     *
     * @param filePath 需要压缩的文件夹
     * @param zos      zip文件输出流
     */
    private void zipFile(String filePath, ZipOutputStream zos) throws IOException {
        //根据文件路径创建文件
        File inputFile = new File(filePath);
        //判断文件是否存在
        if (inputFile.exists()) {
            //判断是否属于文件，还是文件夹
            if (inputFile.isFile()) {
                //创建输入流读取文件
                BufferedInputStream bis = new BufferedInputStream(new FileInputStream(inputFile));
                //将文件写入zip内，即将文件进行打包
                zos.putNextEntry(new ZipEntry(inputFile.getName()));

                //写入文件的方法，同上
                int size = 0;
                //设置读取数据缓存大小
                byte[] buffer = new byte[1024];
                while ((size = bis.read(buffer)) > 0) {
                    zos.write(buffer, 0, size);
                }
                bis.close();
            } else {  //如果是文件夹，写入zip
                File[] files = inputFile.listFiles();
                if (files != null) {
                    for (File fileTem : files) {
                        zipFile(fileTem.toString(), zos);
                    }
                }
            }
        }
        //关闭输入输出流
        zos.closeEntry();
    }


    /**
     * 总时间
     *
     * @param t1
     * @param t2
     * @return
     */
    static String getTotalTime(String t1, String t2) {

        String totalTime = "";

        if (Func.isNotBlank(t1) && Func.isNotBlank(t2)) {

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date d1 = null;
            Date d2 = null;
            try {
                d1 = simpleDateFormat.parse(t1);
                d2 = simpleDateFormat.parse(t2);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
            int s = calLastedTime(d1, d2);
            totalTime = TimeUtil.secondsToTimeHourMinuteSecond2(s);
        }

        return totalTime;
    }

    static int calLastedTime(Date startDate, Date endDate) {
        long a = endDate.getTime();
        long b = startDate.getTime();
        int c = (int) ((a - b) / 1000);
        return c;
    }

    /**
     * 性别
     *
     * @param sex
     * @return
     */
    static String getSex(Integer sex) {
        String sexInfo = "";
        if (Func.isNotEmpty(sex)) {
            sexInfo = sex == 0 ? "男" : "女";
        }
        return sexInfo;
    }
}
