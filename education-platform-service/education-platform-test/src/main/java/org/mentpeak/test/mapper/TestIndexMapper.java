package org.mentpeak.test.mapper;

import com.alicp.jetcache.anno.Cached;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.mentpeak.test.entity.TestIndex;
import org.mentpeak.test.vo.TestIndexVO;

import java.util.List;

/**
 * 指标表 Mapper 接口
 *
 * @author lxp
 * @since 2022-07-12
 */
public interface TestIndexMapper extends BaseMapper<TestIndex> {

	/**
	 * 自定义分页
	 *
	 * @param page
	 * @param testIndex
	 * @return
	 */
	List<TestIndexVO> selectTestIndexPage(IPage page, TestIndexVO testIndex);

	/**
	 * 指标信息
	 * @param dimensionId
	 * @return
	 */
	@Cached(name = "education:indexInfo:",key = "#dimensionId",expire = 60)
	List<TestIndex> indexList(Long dimensionId);
}
