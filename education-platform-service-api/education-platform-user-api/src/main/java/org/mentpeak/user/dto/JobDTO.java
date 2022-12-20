package org.mentpeak.user.dto;

import org.mentpeak.user.entity.Job;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 职业表数据传输对象实体类
 *
 * @author lxp
 * @since 2021-03-27
 */
@Data
@EqualsAndHashCode ( callSuper = true )
public class JobDTO extends Job {
    private static final long serialVersionUID = 1L;

}
