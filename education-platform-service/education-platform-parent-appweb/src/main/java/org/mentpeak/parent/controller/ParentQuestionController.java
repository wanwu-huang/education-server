package org.mentpeak.parent.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import org.mentpeak.core.boot.ctrl.PlatformController;
import org.mentpeak.core.mybatisplus.support.Condition;
import org.mentpeak.core.mybatisplus.support.Query;
import org.mentpeak.core.tool.api.Result;
import org.mentpeak.core.tool.utils.Func;
import org.mentpeak.parent.dto.ParentPaperDTO;
import org.mentpeak.parent.dto.ResponseQuestionDto;
import org.mentpeak.parent.dto.ResultDTO;
import org.mentpeak.parent.entity.ParentQuestion;
import org.mentpeak.parent.service.IParentQuestionService;
import org.mentpeak.parent.vo.ParentQuestionVO;
import org.mentpeak.parent.wrapper.ParentQuestionWrapper;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 家长他评问卷题目信息表 控制器
 *
 * @author lxp
 * @since 2022-06-17
 */
@RestController
@AllArgsConstructor
@RequestMapping("/parentquestion")
@Api(value = "家长他评问卷", tags = "家长他评问卷接口")
public class ParentQuestionController extends PlatformController {

    private IParentQuestionService parentQuestionService;


    /**
     * 详情
     */
    @GetMapping("/detail")
    @ApiOperation(value = "家长他评问卷题目信息详情", notes = "传入parentQuestion", position = 1)
    public Result<ParentPaperDTO> detail(@RequestParam Long studentId) {
        return Result.data(parentQuestionService.selectDetail(studentId));
    }

    /**
     * 新增 家长他评问卷题目信息表
     */
    @PostMapping("/save")
    @ApiOperation(value = "保存做题答案-未完成", notes = "传入parentQuestion", position = 4)
    public Result save(@Valid @RequestBody ResultDTO resultDTO) {
        return Result.status(parentQuestionService.saveResult(resultDTO));
    }

}
