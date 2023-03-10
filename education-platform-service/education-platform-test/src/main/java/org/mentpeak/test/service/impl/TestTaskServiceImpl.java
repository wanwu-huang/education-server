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
 * ??????????????? ???????????????
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
     * ????????????
     */
    private final static String DOWNLOAD_DIR = "download";
    /**
     * ?????????????????????
     */
    private final static String DOWNLOAD_ZIP = "downloadZip";

    @Override
    public IPage<TestTaskVO> selectTestTaskPage(IPage<TestTaskVO> page, TestTaskVO testTask) {
        return page.setRecords(baseMapper.selectTestTaskPage(page, testTask));
    }


    @Override
    public TestApproach addTestTask(TestTaskDTO testTaskDTO) {

        // ??????????????????
        TestTask testTask = new TestTask();
        testTask.setTestApproachId(testTaskDTO.getApproachId());
        testTask.setTaskName(testTaskDTO.getTaskName());
        testTask.setBeginTime(testTaskDTO.getBeginTime());
        testTask.setEndTime(testTaskDTO.getEndTime());
        testTask.setTenantCode(SecureUtil.getTenantCode());
        // ????????????
        testTask.setReportIsVisible(testTaskDTO.getReportIsVisible());
        this.baseMapper.insert(testTask);

        // ?????? ?????? ????????????????????????
        Long[] gradeId = testTaskDTO.getGradeId();
        Arrays.stream(gradeId).forEach(grade -> {
            TestTaskDepartment testTaskDepartment = new TestTaskDepartment();
            testTaskDepartment.setTestTaskId(testTask.getId());
            testTaskDepartment.setTestDepartmentId(grade);
            testTaskDepartmentMapper.insert(testTaskDepartment);
        });

        // ?????? ?????? ???????????????
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
     * ????????????????????????
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
            log.info("????????????????????????");
            e.printStackTrace();
        }
        if (ObjectUtils.isNotEmpty(data)) {
            List<TestTaskDepartment> testTaskDepartments = testTaskDepartmentMapper.selectList(Wrappers.<TestTaskDepartment>lambdaQuery()
                    .eq(TestTaskDepartment::getTestDepartmentId, data.getGrade()));
            // ????????????id
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
                    // ???????????????
                    vo.setStatus(0);
                    // ????????????????????????????????????
                    TestTaskUser testTaskUser = taskUserMapper.selectOne(Wrappers.<TestTaskUser>lambdaQuery()
                            .eq(TestTaskUser::getTestTaskId, testTask.getId())
                            .eq(TestTaskUser::getUserId, userId)
                            .orderByDesc(TestTaskUser::getCreateTime)
                            .last("limit 1"));
                    if (ObjectUtils.isNotEmpty(testTaskUser)) {
                        vo.setStatus(testTaskUser.getCompletionStatus());
                    }
                    // ??????????????????????????????
                    if (vo.getStatus() == 0) {
                        // ????????????????????????????????????????????????
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
        // ??????????????????????????????????????????????????????
        return parse;
    }


    @Override
    public Page<TaskInfoVO> testTaskList(IPage<TestRecordVO> page, TaskSearchDTO taskSearchDTO) {
        PlatformUser user = SecureUtil.getUser();
        String tenantCode = SecureUtil.getTenantCode();
        List<Long> dataIdList = new ArrayList<>();
        Page<TaskInfoVO> taskInfoVoPage = new Page<>();
        if ("2".equals(user.getRoleId())) {
            // ???????????????????????????id
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
            // ???????????????????????????id
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
            task.setTaskStatusName(task.getTaskStatus().equals(0) ? "?????????" : "?????????");
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
                        taskDetail.setSexName(taskDetail.getSex() == 0 ? "???" : "???");
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
        // ?????????????????????
        List<TaskDetailVO> taskDetailVOList = baseMapper.taskDetailListByIdAndCompletionStatus(taskDetailSearchDTO.getTestTaskId(), taskDetailSearchDTO.getCompletionStatus());
        TestTask testTask = baseMapper.selectById(taskDetailSearchDTO.getTestTaskId());
        taskDetailVOList.stream().filter(taskDetailVO -> Func.isNotEmpty(taskDetailVO.getSex())).forEach(taskDetail -> taskDetail.setSexName(taskDetail.getSex() == 0 ? "???" : "???"));
        response.setContentType("application/vnd.ms-excel");
        response.setCharacterEncoding("utf-8");
        try {
            String fileSuffix = taskDetailSearchDTO.getCompletionStatus() == 0 ? "?????????????????????" : "?????????????????????" ;
            String fileName = URLEncoder.encode(testTask.getTaskName() + fileSuffix, "UTF-8");

            response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");

            EasyExcel.write(response.getOutputStream(),
                    TaskDetailVO.class)
                    .sheet("sheet0")
                    // ????????????????????????????????????????????????
                    .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
                    .doWrite(taskDetailVOList);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public boolean deleteTask(Long testTaskId) {

        /**
         * ???????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
         */
        List<TestTaskUser> testTaskUserList = taskUserMapper.selectList(Wrappers.<TestTaskUser>lambdaQuery().eq(TestTaskUser::getTestTaskId, testTaskId));

        if (testTaskUserList.size() > 0) {
            throw new PlatformApiException("??????????????????????????????????????????");
        }

        // ?????? ????????????
        this.baseMapper.deleteById(testTaskId);

        // ?????? ?????? ????????????????????????
        testTaskDepartmentMapper.delete(Wrappers.<TestTaskDepartment>lambdaQuery().eq(TestTaskDepartment::getTestTaskId, testTaskId));

        // ?????? ?????? ???????????????
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

            //????????????????????????
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

        String filePath = DOWNLOAD_DIR + File.separator + uuid + File.separator + taskName + "?????????????????????" + ".xlsx";
        File file = new File(filePath);
        ExcelWriter excelWriter = EasyExcel.write(file).build();


        WriteTable table = new WriteTable();
        table.setTableNo(1);
        table.setHead(head(testQuestionMapper));

        WriteSheet sheet1 = new WriteSheet();
        sheet1.setSheetName("?????????");
        sheet1.setSheetNo(0);

        excelWriter.write(contentData(taskUserList, testOptionMapper), sheet1, table);

        // ?????????
        if (excelWriter != null) {
            excelWriter.finish();
        }

        // ??????
        try {
            downloadZip(request, response, taskName, uuid);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        //??????????????????
        try {
            if (null != fileMkDir) {
                FileUtils.deleteDirectory(fileMkDir);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        long endTime = System.currentTimeMillis();
        log.info("????????????:{}", endTime - startTime);

    }

    @Override
    public void exportScore(TaskDetailExportDTO taskDetailExportDTO, HttpServletRequest request, HttpServletResponse response) {

        ServletOutputStream outputStream = null;
        try {
            outputStream = ExportListener.getServletOutputStream(response, "???????????????");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        ExcelWriter excelWriter = EasyExcel.write(outputStream).build();

        WriteTable table = new WriteTable();
        table.setTableNo(1);
        table.setHead(head(testQuestionMapper));

        WriteSheet sheet1 = new WriteSheet();
        sheet1.setSheetName("?????????");
        sheet1.setSheetNo(0);


        List<TaskUserInfoVO> taskUserInfoVOList = baseMapper.taskUserInfo(taskDetailExportDTO.getTestTaskId(), taskDetailExportDTO.getUserId());
        if (taskUserInfoVOList.size() == 0) {
            // ??????????????????
            TaskUserInfoVO taskUserInfoVO = baseMapper.userInfoById(taskDetailExportDTO.getUserId());
            excelWriter.write(userContentData(taskUserInfoVO, testOptionMapper), sheet1, table);
        } else {
            excelWriter.write(contentData(taskUserInfoVOList, testOptionMapper), sheet1, table);
        }

        // ?????????
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
        String SortInfo = "??????", basicInfo = "????????????", timeInfo = "????????????", qName = "??????????????????";
        //????????????1/2???
        headTitles.add(Lists.newArrayList(SortInfo, SortInfo));
        //????????????1/2???
        headTitles.add(Lists.newArrayList(basicInfo, "??????"));
        headTitles.add(Lists.newArrayList(basicInfo, "?????????"));
        headTitles.add(Lists.newArrayList(basicInfo, "??????"));
        headTitles.add(Lists.newArrayList(basicInfo, "????????????"));
        headTitles.add(Lists.newArrayList(basicInfo, "????????????"));
        headTitles.add(Lists.newArrayList(timeInfo, "???????????????"));
        headTitles.add(Lists.newArrayList(timeInfo, "????????????"));
        headTitles.add(Lists.newArrayList(timeInfo, "????????????"));

        List<Integer> qIds = testQuestionMapper.selectList(Wrappers.<TestQuestion>lambdaQuery().orderByAsc(TestQuestion::getSort)).stream().map(TestQuestion::getSort).collect(Collectors.toList());

        qIds.forEach(q -> {
            headTitles.add(Lists.newArrayList(qName, q.toString()));
        });

        return headTitles;
    }

    /**
     * ??????
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
     * ???????????????
     */
    public void downloadZip(HttpServletRequest request, HttpServletResponse response, String taskName, String uuid) throws IOException {
        OutputStream out = null;
        File zip = null;

        File fileDir = new File(DOWNLOAD_ZIP + File.separator + uuid);
        //????????????????????????
        if (!fileDir.exists()) {
            //???????????????
            fileDir.mkdirs();
        }

        //???????????????????????????????????????????????????
        //?????????????????????????????????zip???
        String zipName = taskName + "?????????????????????".concat(".zip");
        String zipFilePath = DOWNLOAD_ZIP + File.separator + uuid + File.separator + zipName;
        //????????????
        zip = new File(zipFilePath);
        //???????????????????????????
        zip.createNewFile();

        //??????zip???????????????
        ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zip));
        this.zipFile(DOWNLOAD_DIR + File.separator + uuid, zos);
        zos.close();

        //????????????????????????????????????????????????????????????????????????????????????
        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(zipFilePath));
        byte[] buff = new byte[bis.available()];
        bis.read(buff);
        bis.close();

        //IO????????????????????????
        //??????????????????
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/zip");
        //?????????????????????
        String userAgent = request.getHeader("USER-AGENT");

        //???????????????
        if (userAgent.contains("Firefox") || userAgent.contains("firefox")) {
            zipName = new String(zipName.getBytes(), "ISO8859-1");
        } else {
            //???????????????
            zipName = URLEncoder.encode(zipName, "UTF-8");
        }
        //?????????????????????????????????
        response.setHeader("Content-Disposition", "attachment;filename*=utf-8''" + zipName);
        //???????????????????????????????????????????????????????????????
        out = response.getOutputStream();
        //??????????????????
        out.write(buff);

        //???????????????????????????????????????
        FileUtils.deleteDirectory(fileDir);
        zip.delete();

        //????????????
        out.flush();
        //???????????????
        out.close();
    }


    /**
     * ????????????
     *
     * @param filePath ????????????????????????
     * @param zos      zip???????????????
     */
    private void zipFile(String filePath, ZipOutputStream zos) throws IOException {
        //??????????????????????????????
        File inputFile = new File(filePath);
        //????????????????????????
        if (inputFile.exists()) {
            //??????????????????????????????????????????
            if (inputFile.isFile()) {
                //???????????????????????????
                BufferedInputStream bis = new BufferedInputStream(new FileInputStream(inputFile));
                //???????????????zip??????????????????????????????
                zos.putNextEntry(new ZipEntry(inputFile.getName()));

                //??????????????????????????????
                int size = 0;
                //??????????????????????????????
                byte[] buffer = new byte[1024];
                while ((size = bis.read(buffer)) > 0) {
                    zos.write(buffer, 0, size);
                }
                bis.close();
            } else {  //???????????????????????????zip
                File[] files = inputFile.listFiles();
                if (files != null) {
                    for (File fileTem : files) {
                        zipFile(fileTem.toString(), zos);
                    }
                }
            }
        }
        //?????????????????????
        zos.closeEntry();
    }


    /**
     * ?????????
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
     * ??????
     *
     * @param sex
     * @return
     */
    static String getSex(Integer sex) {
        String sexInfo = "";
        if (Func.isNotEmpty(sex)) {
            sexInfo = sex == 0 ? "???" : "???";
        }
        return sexInfo;
    }
}
