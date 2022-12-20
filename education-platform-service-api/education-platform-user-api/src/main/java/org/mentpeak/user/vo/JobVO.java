package org.mentpeak.user.vo;

import org.mentpeak.user.entity.Job;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 职业表视图实体类
 *
 * @author lxp
 * @since 2021-03-27
 */
@Data
@EqualsAndHashCode ( callSuper = true )
@ApiModel ( value = "JobVO对象", description = "职业表" )
public class JobVO extends Job {
    private static final long serialVersionUID = 1L;

}
