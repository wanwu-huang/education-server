package org.mentpeak.test.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.mentpeak.user.entity.MenuData;
import org.mentpeak.user.vo.MenuDataVO;

import java.util.List;

/**
 * 菜单数据关联表 Mapper 接口
 *
 * @author lxp
 * @since 2022-07-12
 */
public interface MenuDataMapper extends BaseMapper<MenuData> {

    /**
     * 自定义分页
     *
     * @param page
     * @param menuData
     * @return
     */
    List<MenuDataVO> selectMenuDataPage(IPage page, MenuDataVO menuData);

}
