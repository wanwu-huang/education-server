package org.mentpeak.test.service;


import org.mentpeak.test.entity.mongo.GroupsReport;

/**
 * 团体报告 服务
 * @author demain_lee
 * @since 2022-08-31
 */
public interface ReportGroupsService {


    /**
     *  查看报告信息
     * @param taskId 任务ID
     * @return 报告信息
     */
    GroupsReport reportInfo(Long taskId);

    /**
     *  生成团体报告
     * @param taskId 任务ID
     * @return
     */
    boolean generateReport(Long taskId);

    /**
     * 报告数据
     * @param taskId
     * @return
     */
    GroupsReport generateBaseReport(Long taskId);
}
