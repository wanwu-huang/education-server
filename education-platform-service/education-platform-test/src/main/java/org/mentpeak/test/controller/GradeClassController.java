package org.mentpeak.test.controller;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.mentpeak.core.boot.ctrl.PlatformController;
import org.mentpeak.core.tool.api.Result;
import org.mentpeak.core.tool.utils.Func;
import org.mentpeak.test.entity.GradeClass;
import org.mentpeak.test.service.IGradeClassService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 年级-班级对应关联表 控制器
 *
 * @author lxp
 * @since 2022-08-12
 */
@RestController
@AllArgsConstructor
@RequestMapping("/")
@Api(value = "年级-班级对应关联表", tags = "年级-班级对应关联表接口")
public class GradeClassController extends PlatformController {

    private IGradeClassService gradeClassService;


    @ApiOperation(value = "插入关联数据")
    @ApiOperationSupport(order = 1, author = "domain_lee")
    @GetMapping("/saveData")
    public Result<?> saveData() {
        List<Integer> gradeIdList = Arrays.asList(21, 22, 23, 24, 25, 26, 31, 32, 33, 41, 42, 43, 51, 52, 53, 54);
        List<Integer> classIdList = Arrays.asList(55, 56, 57, 58, 59, 60, 61, 62, 63, 64, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 91, 92, 93, 94, 95, 96, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122, 123, 124, 125, 126, 127, 128, 129, 130, 131, 132, 133, 134, 135, 136, 137, 138, 139, 140, 141, 142, 143, 144, 145, 146, 147, 148, 149, 150, 151, 152, 153, 154);

        List<GradeClass> list = new ArrayList<>();

        gradeIdList.forEach(grade -> {
            classIdList.forEach(classId -> {
                GradeClass gradeClass = new GradeClass();
                gradeClass.setGradeId(Func.toLong(grade));
                gradeClass.setClassId(Func.toLong(classId));
                list.add(gradeClass);
            });
        });

        return status(gradeClassService.saveBatch(list));
    }

}
