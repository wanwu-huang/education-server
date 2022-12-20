package org.mentpeak.test.mapper;

import com.alicp.jetcache.anno.Cached;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.mentpeak.test.entity.TestPaperRecord;
import org.mentpeak.test.vo.TestPaperRecordVO;
import org.mentpeak.test.vo.TestRecordVO;

import java.util.List;

/**
 * 用户问卷测试记录表 Mapper 接口
 *
 * @author lxp
 * @since 2022-07-13
 */
public interface TestPaperRecordMapper extends BaseMapper<TestPaperRecord> {

    /**
     * 自定义分页
     *
     * @param page
     * @param testPaperRecord
     * @return
     */
    List<TestPaperRecordVO> selectTestPaperRecordPage(IPage page, TestPaperRecordVO testPaperRecord);

    Page<TestRecordVO> getList(IPage<TestRecordVO> page, @Param("userId") Long userId);

    /**
     * 报告记录信息
     * @param userId
     * @param taskId
     * @return
     */
    @Cached(name = "education:paperRecordList:",expire = 60)
    List<TestPaperRecord> paperRecordList(@Param("taskId") Long taskId,@Param("userId") Long userId);
}
