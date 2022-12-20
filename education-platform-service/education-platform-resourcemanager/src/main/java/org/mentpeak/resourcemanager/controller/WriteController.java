package org.mentpeak.resourcemanager.controller;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.read.builder.ExcelReaderBuilder;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.mentpeak.core.boot.ctrl.PlatformController;
import org.mentpeak.core.boot.file.PlatformFile;
import org.mentpeak.core.tool.api.Result;
import org.mentpeak.resourcemanager.listener.ParentReadListener;
import org.mentpeak.resourcemanager.listener.dto.ParentExcelDto;
import org.mentpeak.resourcemanager.service.IParentOptionService;
import org.mentpeak.resourcemanager.service.IParentQuestionService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;

/**
 * 家长他评问卷题支信息表 控制器
 *
 * @author lxp
 * @since 2022-06-17
 */
@RestController
@AllArgsConstructor
@RequestMapping("/excel")
@Api(value = "Excel控制器", tags = "Excel控制器")
public class WriteController extends PlatformController {

	private IParentOptionService parentOptionService;

	private IParentQuestionService parentQuestionService;

	/**
	 * 新增 家长他评问卷题支信息表
	 */
	@PostMapping("/save")
	@ApiOperation(value = "新增", notes = "传入parentOption")
//	@Transactional(rollbackFor = Exception.class)
	public Result<?> save(@Valid @RequestBody MultipartFile excelFile) throws IOException {
		EasyExcel.read(excelFile.getInputStream(), ParentExcelDto.class, new ParentReadListener(parentOptionService, parentQuestionService)).sheet("家长他评问卷").doRead();
		return Result.data("成功");
	}

}
