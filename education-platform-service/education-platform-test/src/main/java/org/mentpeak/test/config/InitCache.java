package org.mentpeak.test.config;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import lombok.extern.slf4j.Slf4j;
import org.mentpeak.test.entity.mongo.ClassReport;
import org.mentpeak.test.entity.mongo.GradeReport;
import org.mentpeak.test.service.ITestTeacherPaperService;
import org.mentpeak.test.strategy.CommonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import java.io.*;

/**
 * @author hzl
 * @data 2022年07月14日17:24
 * 初始化题库
 */
@Component
@Slf4j
@Order(-1)
public class InitCache implements ApplicationRunner {

    @Autowired
    private CommonService commonService;

    @Autowired
    private ITestTeacherPaperService testTeacherPaperService;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        commonService.setAllToRedis();
        // 教师评定
        testTeacherPaperService.dataToCache();

        // 班级报告
        saveClass();
        // 年级报告
        saveGrade();

    }

    void saveClass() {
        ClassReport byId = mongoTemplate.findById("classReport", ClassReport.class, "classReport");
        if (ObjectUtils.isEmpty(byId)) {
            log.info("===缓存班级报告空数据===");
            String fileName = "classReport.json";

            InputStream in = this.getClass().getClassLoader().getResourceAsStream(fileName);
            try {
                getFileContent(in, 1);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    void saveGrade() {
        GradeReport gradeReport = mongoTemplate.findById("gradeReport", GradeReport.class, "gradeReport");
        if (ObjectUtils.isEmpty(gradeReport)) {
            log.info("===缓存年级报告空数据===");
            String fileName = "gradeReport.json";

            InputStream in = this.getClass().getClassLoader().getResourceAsStream(fileName);
            try {
                getFileContent(in, 2);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 根据文件路径读取文件内容
     *
     * @param fileInPath
     * @throws IOException
     */
    public void getFileContent(Object fileInPath, int type) throws IOException {
        String jsonStr = "";
        BufferedReader br = null;
        if (fileInPath == null) {
            return;
        }
        if (fileInPath instanceof String) {
            br = new BufferedReader(new FileReader(new File((String) fileInPath)));
        } else if (fileInPath instanceof InputStream) {
            br = new BufferedReader(new InputStreamReader((InputStream) fileInPath));
        }
        int ch = 0;
        StringBuffer sb = new StringBuffer();
        while ((ch = br.read()) != -1) {
            sb.append((char) ch);
//            System.out.println(line);
        }
        br.close();
        jsonStr = sb.toString();

        if (type == 1) {
            // 报告空数据
            ClassReport classReport = JSONObject.parseObject(jsonStr, ClassReport.class);
            classReport.setId("classReport");
            mongoTemplate.save(classReport, "classReport");
        } else {
            // 报告空数据
            GradeReport report = JSONObject.parseObject(jsonStr, GradeReport.class);
            report.setId("gradeReport");
            mongoTemplate.save(report, "gradeReport");
        }
    }

}
