package org.mentpeak.parent.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 试卷表实体类
 *
 * @author lxp
 * @since 2022-05-09
 */
@Data
@TableName("parent_paper")
@ApiModel(value = "Paper对象", description = "试卷表")
public class ParentPaper implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @ApiModelProperty(value = "ID")
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    /**
     * 租户编号
     */
    @ApiModelProperty(value = "租户编号")
    private String tenantCode;
    /**
     * 问卷ID
     */
    @ApiModelProperty(value = "问卷ID")
    private Long questionnaireId;
    /**
     * 孩子ID
     */
    @ApiModelProperty(value = "孩子ID")
    private Long childId;
    /**
     * 开始答卷时间
     */
    @ApiModelProperty(value = "开始答卷时间")
    private LocalDateTime startTime;
    /**
     * 结束答卷时间
     */
    @ApiModelProperty(value = "结束答卷时间")
    private LocalDateTime finishTime;
    /**
     * 当前作答题干 ID
     */
    @ApiModelProperty(value = "当前作答题干 ID")
    private Long questionId;
    /**
     * 是否完成当前作答题干 (0未完成 1完成)
     */
    @ApiModelProperty(value = "是否完成当前作答题干 (0未完成 1完成)")
    private Integer isFinishQuestion;
    /**
     * 是否完成此试卷(0未完成 1完成)
     */
    @ApiModelProperty(value = "是否完成此试卷(0未完成 1完成)")
    private Integer isFinish;
    /**
     * 是否关注(0不关注 1关注)
     */
    @ApiModelProperty(value = "是否关注(0不关注 1关注)")
    private Integer isFocus;
    /**
     * 是否禁用：0禁用，1 可用
     */
    @ApiModelProperty(value = "是否禁用：0禁用，1 可用")
    private Integer status;
    /**
     * 创建人
     */
    @ApiModelProperty(value = "创建人")
    private Long createUser;
    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createTime;
    /**
     * 更新人
     */
    @ApiModelProperty(value = "更新人")
    private Long updateUser;
    /**
     * 更新时间
     */
    @ApiModelProperty(value = "更新时间")
    private LocalDateTime updateTime;
    /**
     * 是否删除
     */
    @ApiModelProperty(value = "是否删除")
    private Integer isDeleted;


}
