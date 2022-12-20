package org.mentpeak.test.entity.mongo;

import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.util.List;
import lombok.Builder;
import lombok.Data;
import org.mentpeak.test.vo.GFollowUserVO;

/**
 * 团体报告数据
 *
 * @author demain_lee
 * @since 2022-08-31
 */
@Data
@Builder
public class GroupsReport implements Serializable {

  private static final long serialVersionUID = 1L;

  @ApiModelProperty(value = "是否可看 0:否 1:是")
  private Integer isVisible;

  /**
   * 主键
   */
  private String id;

  /**
   * 任务ID
   */
  private Long taskId;

  /**
   * 报告名字
   */
  @ApiModelProperty(value = "报告名字")
  private String reportName;


  @ApiModelProperty(value = "测评完成情况")
  private EvaluationCompletion evaluationCompletion;

  @ApiModelProperty(value = "基本信息")
  private BasicInformation basicInformation;

  @ApiModelProperty(value = "心理健康评级")
  private MentalHealthRatings mentalHealthRatings;

  @ApiModelProperty(value = "测评概况")
  private TestOverview testOverview;

  @ApiModelProperty(value = "学习状态")
  private BaseData learningStatus;

  @ApiModelProperty(value = "品行表现")
  private BaseData behavior;

  @ApiModelProperty(value = "心理韧性")
  private BaseData mentalToughness;

  @ApiModelProperty(value = "综合压力")
  private StressIndexData stressIndex;

  @ApiModelProperty(value = "情绪指数")
  private BaseResultData emotionalIndex;

  @ApiModelProperty(value = "睡眠指数")
  private BaseData sleepIndex;

  @ApiModelProperty(value = "创建时间")
  private String createTime;


  /**
   * 测评完成情况
   */
  @Data
  public static class EvaluationCompletion {

    @ApiModelProperty(value = "已测人数")
    private Integer totalTestPeople;

    @ApiModelProperty(value = "未测人数")
    private Integer totalNoTestPeople;

    @ApiModelProperty(value = "无效人数")
    private Integer totalInvalidPeople;

    @ApiModelProperty(value = "完成率")
    private String totalCompletionRate;

    @ApiModelProperty(value = "测评完成情况 表格数据")
    private List<ParticipatingGrade> participatingGrade;

  }


  /**
   * 测评完成情况 表格数据
   */
  @Data
  @Builder
  public static class ParticipatingGrade {

    @ApiModelProperty(value = "参与年级")
    private String gradeName;

    @ApiModelProperty(value = "实际人数")
    private Integer totalPeople;

    @ApiModelProperty(value = "已测人数")
    private Integer testPeople;

    @ApiModelProperty(value = "未测人数")
    private Integer noTestPeople;

    @ApiModelProperty(value = "无效人数")
    private Integer invalidPeople;

    @ApiModelProperty(value = "完成率")
    private String completionRate;

  }


  /**
   * 测评完成情况 表格数据
   */
  @Data
  public static class BasicInformation {

    @ApiModelProperty(value = "参与年级")
    private TypeData grade;

    @ApiModelProperty(value = "性别")
    private TypeData sex;

    @ApiModelProperty(value = "家庭结构")
    private TypeData familyStructure;

    @ApiModelProperty(value = "是否与父母生活")
    private TypeData livingWithParents;

  }

  /**
   * 图表类型数据
   */
  @Data
  public static class TypeData {

    @ApiModelProperty(value = "类别名字")
    private String title;

    @ApiModelProperty(value = "图标数据")
    private List<ChartData> chartData;

  }

  /**
   * 图表数据就
   */
  @Data
  public static class ChartData {

    @ApiModelProperty(value = "名字")
    private String name;

    @ApiModelProperty(value = "值")
    private Double value;

    @ApiModelProperty(value = "图表颜色")
    private String chartColor;

  }


  /**
   * 关注等级
   */
  @Data
  public static class MentalHealthRatings {

    @ApiModelProperty(value = "建议关注等级")
    private List<TypeData> attentionLevel;

    @ApiModelProperty(value = "关注等级分布表格数据")
    private List<LevelTable> attentionLevelTable;

    @ApiModelProperty(value = "年级建议关注等级分布")
    private List<AttentionLevelDistribution> attentionLevelDistribution;

    @ApiModelProperty(value = "建议关注学生名单")
//    private List<AttentionStudent> attentionStudent;
    private List<GFollowUserVO> attentionStudent;

    @ApiModelProperty(value = "心理危机预警")
    private TypeData warningLevel;

    @ApiModelProperty(value = "预警等级分布表格数据")
    private List<LevelTable> warningLevelTable;

    @ApiModelProperty(value = "年级危机预警等级分布")
    private List<WarningLevelDistribution> warningLevelDistribution;

    @ApiModelProperty(value = "危机预警学生名单")
//    private List<WarningStudent> warningStudent;
    private List<GFollowUserVO> warningStudent;

  }


  /**
   * 等级分布表格数据
   */
  @Data
  public static class LevelTable {

    @ApiModelProperty(value = "id")
    private String id;
    @ApiModelProperty(value = "级别")
    private String attentionLevel;
    @ApiModelProperty(value = "人数")
    private String peopleCount;
    @ApiModelProperty(value = "全校占比")
    private String universityPercentage;
  }


  /**
   * 年级建议关注等级分布
   */
  @Data
  @Builder
  public static class AttentionLevelDistribution {

    @ApiModelProperty(value = "id")
    private Integer gradeId;
    @ApiModelProperty(value = "参与年级")
    private String gradeName;
    @ApiModelProperty(value = "3级关注人数")
    private Integer threeAttentionLevelCount;
    @ApiModelProperty(value = "3级关注人数（年级占比）")
    private String threeAttentionLevel;
    @ApiModelProperty(value = "2级关注人数")
    private Integer twoAttentionLevelCount;
    @ApiModelProperty(value = "2级关注人数（年级占比）")
    private String twoAttentionLevel;
    @ApiModelProperty(value = "1级关注人数")
    private Integer oneAttentionLevelCount;
    @ApiModelProperty(value = "1级关注人数（年级占比）")
    private String oneAttentionLevel;
    @ApiModelProperty(value = "良好人数")
    private Integer zeroAttentionLevelCount;
    @ApiModelProperty(value = "良好人数（年级占比）")
    private String zeroAttentionLevel;
    @ApiModelProperty(value = "任务ID")
    private Long taskId;
  }

  /**
   * 年级建议关注等级分布
   */
  @Data
  public static class AttentionStudent {

    @ApiModelProperty(value = "id")
    private String id;
    @ApiModelProperty(value = "参与班级")
    private String className;
    @ApiModelProperty(value = "3级关注")
    private List<UserData> threeAttention;

  }


  /**
   * 用户基础数据
   */
  @Data
  public static class UserData {

    @ApiModelProperty(value = "用户Id")
    private Long userId;

    @ApiModelProperty(value = "名字")
    private String name;
  }


  /**
   * 年级危机预警等级分布
   */
  @Data
  @Builder
  public static class WarningLevelDistribution {

    @ApiModelProperty(value = "id")
    private Integer gradeId;
    @ApiModelProperty(value = "参与年级")
    private String gradeName;
    @ApiModelProperty(value = "3级预警人数")
    private Integer threeWarningLevelCount;
    @ApiModelProperty(value = "3级预警人数（年级占比）")
    private String threeWarningLevel;
    @ApiModelProperty(value = "2级预警人数")
    private Integer twoWarningLevelCount;
    @ApiModelProperty(value = "2级预警人数（年级占比）")
    private String twoWarningLevel;
    @ApiModelProperty(value = "1级预警人数")
    private Integer oneWarningLevelCount;
    @ApiModelProperty(value = "1级预警人数（年级占比）")
    private String oneWarningLevel;
    @ApiModelProperty(value = "未发现人数")
    private Integer zeroWarningLevelCount;
    @ApiModelProperty(value = "未发现人数（年级占比）")
    private String zeroWarningLevel;
    @ApiModelProperty(value = "任务ID")
    private Long taskId;
  }

  /**
   * 危机预警学生名单
   */
  @Data
  public static class WarningStudent {

    @ApiModelProperty(value = "id")
    private String id;
    @ApiModelProperty(value = "参与年级")
    private String gradeName;
    @ApiModelProperty(value = "3级预警")
    private List<UserData> threeWarning;
    @ApiModelProperty(value = "2级预警")
    private List<UserData> twoWarning;
    @ApiModelProperty(value = "1级预警")
    private List<UserData> oneWarning;

  }

  /**
   * 测评概况
   */
  @Data
  public static class TestOverview{

    @ApiModelProperty(value = "积极")
    private List<OverviewData> active;

    @ApiModelProperty(value = "消极")
    private List<OverviewData> negative;
  }

  /**
   * 概况数据
   */
  @Data
  public static class OverviewData {

    @ApiModelProperty(value = "名称")
    private String title;

    @ApiModelProperty(value = "结果")
    private String result;

    @ApiModelProperty(value = "分数")
    private String score;

    @ApiModelProperty(value = "字体颜色")
    private String fontColor;

  }


  /**
   * 维度数据
   */
  @Data
  public static class BaseData {

    @ApiModelProperty(value = "名称")
    private TotalResult totalResult;

    @ApiModelProperty(value = "图表数据")
    private List<ChartResultData> chartData;

    @ApiModelProperty(value = "图表表格数据")
    private List<TableData> tableData;

  }


  @Data
  public static class TotalResult {

    @ApiModelProperty(value = "名称")
    private String title;

    @ApiModelProperty(value = "图表名称")
    private String label;

    @ApiModelProperty(value = "分数")
    private String score;

    @ApiModelProperty(value = "结果")
    private String result;

    @ApiModelProperty(value = "字体颜色")
    private String fontColor;

    @ApiModelProperty(value = "图表颜色")
    private String chartColor;

  }

  @Data
  public static class TypeResultData {

    @ApiModelProperty(value = "类别名字")
    private String title;

    @ApiModelProperty(value = "图标数据")
    private List<ChartResultData> chartData;

  }

  @Data
  public static class BaseResultData {

    @ApiModelProperty(value = "名称")
    private TotalResult totalResult;

    @ApiModelProperty(value = "图表数据")
    private List<TypeResultData> chartData;

    @ApiModelProperty(value = "图表表格数据")
    private List<TableData> tableData;

  }


  @Data
  public static class ChartResultData {

    @ApiModelProperty(value = "标题")
    private String title;

    @ApiModelProperty(value = "风险指数")
    private Integer riskIndex;

    @ApiModelProperty(value = "结果")
    private String result;

    @ApiModelProperty(value = "分数")
    private String score;

    @ApiModelProperty(value = "描述")
    private String resultDes;

    @ApiModelProperty(value = "字体颜色")
    private String fontColor;

    @ApiModelProperty(value = "图表颜色")
    private String chartColor;

  }

  @Data
  public static class TableData {

    @ApiModelProperty(value = "影响程度排名")
    private Integer rank;

    @ApiModelProperty(value = "指标")
    private String index;

    @ApiModelProperty(value = "很差人数（占比）")
    private String veryPoor;

    @ApiModelProperty(value = "较差人数（占比）")
    private String ratherPoor;

    @ApiModelProperty(value = "一般人数（占比）")
    private String general;

    @ApiModelProperty(value = "良好人数（占比）")
    private String good;

    @ApiModelProperty(value = "影响程度排名分数")
    private String rankScore;

  }


  /**
   * 综合压力数据
   */
  @Data
  public static class StressIndexData extends BaseData {

    @ApiModelProperty(value = "事件分布")
    private List<ImpactEvents> impactEvents;

    @ApiModelProperty(value = "图表数据")
    private List<TypeResultData> chartList;

  }

  @Data
  public static class ImpactEvents {

    @ApiModelProperty(value = "影响程度排名")
    private String rank;

    @ApiModelProperty(value = "压力事件")
    private String stressEvents;

    @ApiModelProperty(value = "影响人数比例")
    private String peoplePercentage;

  }



}
