package org.mentpeak.resourcemanager.listener;


import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import lombok.extern.slf4j.Slf4j;
import org.mentpeak.core.tool.utils.Func;
import org.mentpeak.resourcemanager.entity.ParentOption;
import org.mentpeak.resourcemanager.entity.ParentQuestion;
import org.mentpeak.resourcemanager.listener.dto.ParentExcelDto;
import org.mentpeak.resourcemanager.service.IParentOptionService;
import org.mentpeak.resourcemanager.service.IParentQuestionService;

/**
 * @author lxp
 * @date 2022/06/18 22:27
 **/
@Slf4j
public class ParentReadListener implements ReadListener<ParentExcelDto> {
	/**
	 * 每隔1条存储数据库，实际使用中可以100条，然后清理list ，方便内存回收
	 */
	private int count = 0;

	private IParentOptionService parentOptionService;

	private IParentQuestionService parentQuestionService;

	private ParentExcelDto preExcelDto;


	public ParentReadListener(IParentOptionService parentOptionService, IParentQuestionService parentQuestionService) {
		this.parentOptionService = parentOptionService;
		this.parentQuestionService = parentQuestionService;
	}

	@Override
	public void invoke(ParentExcelDto parentExcelDto, AnalysisContext analysisContext) {
		log.info("解析到一条数据:{}", Func.toJson(parentExcelDto));
		count++;
		saveData(parentExcelDto);
	}

	@Override
	public void doAfterAllAnalysed(AnalysisContext analysisContext) {
		log.info("所有数据解析完成！");
	}


	private void saveData(ParentExcelDto currExcelDto) {
		log.info("{}条数据，开始存储数据库", count);
		//省缺字段
		int sort = 1;
		//保存问卷
		ParentExcelDto parentExcelDto = currExcelDto;
		Long questionnaireId = parentExcelDto.getQuestionnaireId();
		//保存题干
		if (!Func.isEmpty(questionnaireId)) {
			questionnaireId = preExcelDto.getQuestionnaireId();
		} else {
			sort = 1;
			questionnaireId = preExcelDto.getQuestionnaireId();
		}
		Long qId = parentExcelDto.getQId();
		if (Func.isEmpty(qId)) {
			qId = preExcelDto.getQId();
		}
		//保存题型
		ParentQuestion parentQuestion = new ParentQuestion();
		if (!Func.isEmpty(currExcelDto.getTitle())) {
			parentQuestion.setQuestionnaireId(questionnaireId);
			parentQuestion.setParentId(Func.toLong(currExcelDto.getParentId())== -1? null : Func.toLong(currExcelDto.getParentId()));
			parentQuestion.setId(qId);
			parentQuestion.setTitle(parentExcelDto.getTitle());
			parentQuestion.setQuestionType(parentExcelDto.getQuestionType());
			parentQuestion.setCreateUser(1L);
			parentQuestion.setUpdateUser(1L);
			//保存并获取ID实体中
			parentQuestionService.save(parentQuestion);
		}
		//保存题支
		ParentOption parentOption = new ParentOption();
		if (Func.isEmpty(parentQuestion.getId())) {
			parentOption.setQuestionId(preExcelDto.getQId());
		}else{
			parentOption.setQuestionId(Func.toLong(parentQuestion.getId()));
		}
		parentOption.setSort(sort);
		parentOption.setTitle(parentExcelDto.getOtitle());
		parentOption.setScore(Func.toInt(parentExcelDto.getScore()) == -1 ? null : Func.toInt(parentExcelDto.getScore()));
//		parentOption.setExtParam(parentExcelDto.getExtParam());
		parentOption.setCreateUser(1L);
		parentOption.setUpdateUser(1L);
		//保存题支扩展类型
		//存储数据库
		parentOptionService.save(parentOption);
		sort++;
		if (!Func.isEmpty(currExcelDto.getTitle())) {
			this.preExcelDto = currExcelDto;
			this.preExcelDto.setQId(Func.toLong(parentQuestion.getId()));
		}
		log.info("存储数据库成功!");
	}
}
