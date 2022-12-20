package org.mentpeak.parent.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.mentpeak.core.mybatisplus.base.BaseServiceImpl;
import org.mentpeak.parent.entity.Letters;
import org.mentpeak.parent.mapper.LettersMapper;
import org.mentpeak.parent.service.ILettersService;
import org.mentpeak.parent.vo.LettersVO;
import org.springframework.stereotype.Service;

/**
 * 家长端-匿名信表 服务实现类
 *
 * @author lxp
 * @since 2022-06-15
 */
@Service
public class LettersServiceImpl extends BaseServiceImpl<LettersMapper, Letters> implements ILettersService {

}
