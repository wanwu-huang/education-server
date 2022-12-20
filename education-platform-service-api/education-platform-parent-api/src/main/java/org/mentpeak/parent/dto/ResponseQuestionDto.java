package org.mentpeak.parent.dto;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.mentpeak.test.LogicQuestion;
import org.mentpeak.test.base.BaseOption;

/**
 * @author lxp
 * @date 2022/06/19 13:24
 **/
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel(value = "家长他评问卷题干详情", description = "家长他评问卷题干详情")
public class ResponseQuestionDto<T extends BaseOption> extends LogicQuestion<T> {
//	{
//		"qid":"",
//			"qTitle":"",
//			"sort":1,
//			"qType":1,
//			"explain":"xxx",
//			"isJump":"跳题逻辑",
//			"nextQid":"跳题逻辑题干ID",
//			"subQuestions":[
//		{
//			"qid":"",
//				"qTitle":"",
//				"sort":1,
//				"qType":1,
//				"optionList":[
//			{
//				"oId":"key",
//					"oTitle":"value",
//					"oType": ""
//			},{
//			"oId":"0",
//					"oTitle":"",
//					"oType":""
//		}
//		]
//		}
//	],
//		"optionList":[
//		{
//			"oId":"",
//				"oTitle":"",
//				"oType": "",
//				"jumpLogic":{
//			"logicType":"1",
//					"qIds":["",""],
//			"nextQid":"跳下一题ID"
//		}
//		},{
//		"oId":"0",
//				"oTitle":"",
//				"oType":""
//	}
//	]
//	}


}
