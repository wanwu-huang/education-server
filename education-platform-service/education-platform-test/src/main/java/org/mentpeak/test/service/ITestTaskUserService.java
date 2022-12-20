package org.mentpeak.test.service;

import com.alicp.jetcache.anno.CacheType;
import com.alicp.jetcache.anno.Cached;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.mentpeak.core.mybatisplus.base.BaseService;
import org.mentpeak.test.dto.TaskSearchDTO;
import org.mentpeak.test.entity.TestTaskUser;
import org.mentpeak.test.vo.FollowVO;
import org.mentpeak.test.vo.TaskWarnVO;
import org.mentpeak.test.vo.TestRecordVO;
import org.mentpeak.test.vo.TestTaskUserVO;

import java.util.concurrent.TimeUnit;

/**
 * 测评任务用户关联表 服务类
 *
 * @author lxp
 * @since 2022-07-12
 */
public interface ITestTaskUserService extends BaseService<TestTaskUser> {

    /**
     * 自定义分页
     *
     * @param page
     * @param testTaskUser
     * @return
     */
    IPage<TestTaskUserVO> selectTestTaskUserPage(IPage<TestTaskUserVO> page, TestTaskUserVO testTaskUser);

    /**
     * 预警管理 - 任务列表
     *
     * @param page          page
     * @param taskSearchDTO dto
     * @return info
     */
    Page<TaskWarnVO> warnList(IPage<TestRecordVO> page, TaskSearchDTO taskSearchDTO);

    /**
     * 预警管理-关注等级列表
     *
     * @param page
     * @param followId
     * @return com.baomidou.mybatisplus.core.metadata.IPage<org.mentpeak.test.vo.FollowVO>
     * @author hzl
     * @date 2022/7/19 16:39
     */
    @Cached(name = "ITestTaskUserService:", expire = 10, timeUnit = TimeUnit.SECONDS, cacheType = CacheType.BOTH,
            key = "#taskId+':'+#followId+':'+#page.size+':'+#page.current")
    IPage<FollowVO> followList(IPage<FollowVO> page, Long followId, Long taskId);

}
