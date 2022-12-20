package org.mentpeak.test.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 年级-班级对应关联表实体类
 *
 * @author lxp
 * @since 2022-08-12
 */
@Data
@ApiModel(value = "GradeClass对象", description = "年级-班级对应关联表")
public class GradeClass implements Serializable {

    private static final long serialVersionUID = 1L;

  @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    /**
     * 年级id
     */
    @ApiModelProperty(value = "年级id")
      private Long gradeId;
    /**
     * 班级id
     */
    @ApiModelProperty(value = "班级id")
      private Long classId;


}
