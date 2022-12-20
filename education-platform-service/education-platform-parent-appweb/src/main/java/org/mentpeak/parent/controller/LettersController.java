package org.mentpeak.parent.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.mentpeak.core.auth.utils.SecureUtil;
import org.mentpeak.core.boot.ctrl.PlatformController;
import org.mentpeak.core.mybatisplus.support.Condition;
import org.mentpeak.core.tool.api.Result;
import org.mentpeak.parent.dto.LettersDTO;
import org.mentpeak.parent.entity.Letters;
import org.mentpeak.parent.service.ILettersService;
import org.mentpeak.parent.vo.LettersVO;
import org.mentpeak.parent.wrapper.LettersWrapper;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 家长端-匿名信表 控制器
 *
 * @author lxp
 * @since 2022-06-15
 */
@RestController
@AllArgsConstructor
@RequestMapping("/letters")
@Api(value = "家长端-匿名信表", tags = "家长端-匿名信表接口")
public class LettersController extends PlatformController {

    private ILettersService lettersService;


    /**
     * 详情
     */
    @GetMapping("/detail")
    @ApiOperation(value = "详情", notes = "传入letters", position = 1)
    public Result<LettersVO> detail(Letters letters) {
        Letters detail = lettersService.getOne(Condition.getQueryWrapper(letters));
        LettersWrapper lettersWrapper = new LettersWrapper();
        return Result.data(lettersWrapper.entityVO(detail));
    }


    /**
     * 新增 家长端-匿名信表
     */
    @PostMapping("/save")
    @ApiOperation(value = "提交", notes = "传入letters", position = 4)
    public Result<?> save(@Valid @RequestBody LettersDTO lettersDTO) {
        Letters letters = new Letters();
        letters.setLettersContent(lettersDTO.getLettersContent());
        letters.setSchool(lettersDTO.getSchoolName());
        letters.setCreateUser(SecureUtil.getUserId());
        return Result.status(lettersService.save(letters));
    }

}
