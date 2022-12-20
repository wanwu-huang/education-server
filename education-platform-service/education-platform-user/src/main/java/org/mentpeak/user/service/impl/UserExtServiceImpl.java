package org.mentpeak.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.mentpeak.core.auth.PlatformUser;
import org.mentpeak.core.auth.utils.SecureUtil;
import org.mentpeak.core.mybatisplus.support.Condition;
import org.mentpeak.core.tool.api.Result;
import org.mentpeak.core.tool.utils.Func;
import org.mentpeak.core.tool.utils.ObjectUtil;
import org.mentpeak.core.tool.utils.StringUtil;
import org.mentpeak.user.entity.*;
import org.mentpeak.user.mapper.*;
import org.mentpeak.user.service.IUserExtService;
import org.mentpeak.user.vo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 用户信息扩展表 服务实现类
 *
 * @author lxp
 * @since 2021-03-27
 */
@Service
public class UserExtServiceImpl extends ServiceImpl<UserExtMapper, UserExt> implements IUserExtService {

    @Autowired
    private SysProvinceMapper provinceMapper;
    @Autowired
    private SysCityMapper cityMapper;
    @Autowired
    private SysAreaMapper areaMapper;
    @Autowired
    private UserParamMapper paramMapper;

    @Override
    public IPage<UserExtVO> selectUserExtPage(
            IPage<UserExtVO> page,
            UserExtVO userExt) {
        return page.setRecords(baseMapper.selectUserExtPage(page,
                userExt));
    }

    @Override
    public Result<List<SysProvince>> findPrivince() {
        SysProvince province = new SysProvince();
        List<SysProvince> sysProvinces = provinceMapper.selectList(Condition.getQueryWrapper(province));
        return Result.data(sysProvinces);
    }

    @Override
    public Result<List<SysCity>> findCity(SysProvince province) {
        SysCity city = new SysCity();
        city.setProvinceCode(province.getProvinceCode());
        List<SysCity> sysCities = cityMapper.selectList(Condition.getQueryWrapper(city));
        return Result.data(sysCities);
    }

    @Override
    public Result<List<SysArea>> findArea(SysCity city) {
        SysArea area = new SysArea();
        area.setCityCode(city.getCityCode());
        List<SysArea> sysAreas = areaMapper.selectList(Condition.getQueryWrapper(area));
        return Result.data(sysAreas);
    }

    @Override
    public Result findImgUrl() {
        UserParam param = paramMapper.selectOne(new QueryWrapper<UserParam>().eq("param_key",
                "default.headerimage"));
        String paramValue = param.getParamValue();
        String[] split = Func.split(paramValue,
                ",");
        List<HeadImageVO> list = new ArrayList<>();
        HeadImageVO headImageVO = null;
        for (String s : split) {
            headImageVO = new HeadImageVO();
            headImageVO.setHeadUrl(s);
            list.add(headImageVO);
        }
        return Result.data(list);
    }

    @Override
    public Result getAddress() {
        List<SysProvince> provinceList = findPrivince().getData();
        List<AddressVo> addressVoList = new ArrayList<>();
        AddressVo addressVo = null;
        for (SysProvince sysProvince : provinceList) {
            List<SysCity> data = findCity(sysProvince).getData();
            addressVo = new AddressVo();
            addressVo.setValue(sysProvince.getProvinceCode());
            addressVo.setLabel(sysProvince.getProvinceName());
            List<CityVo> cityVoList = new ArrayList<>();
            CityVo cityVo = null;
            for (SysCity datum : data) {
                cityVo = new CityVo();
                cityVo.setValue(datum.getCityCode());
                cityVo.setLabel(datum.getCityName());
                cityVoList.add(cityVo);
            }
            addressVo.setChildren(cityVoList);
            addressVoList.add(addressVo);
        }

        // TODO:redis缓存
        return Result.data(addressVoList);
    }

    @Override
    public SysProvince getProvince(String cityCode) {

        // 根据市编码查询 市信息
        SysCity city = new SysCity();
        city.setCityCode(cityCode);
        SysCity sysCity = cityMapper.selectOne(Condition.getQueryWrapper(city));
        SysProvince sysProvince = null;
        if (ObjectUtil.isNotEmpty(sysCity)) {
            SysProvince province = new SysProvince();
            province.setProvinceCode(sysCity.getProvinceCode());
            sysProvince = provinceMapper.selectOne(Condition.getQueryWrapper(province));
        }

        return sysProvince;
    }

}
