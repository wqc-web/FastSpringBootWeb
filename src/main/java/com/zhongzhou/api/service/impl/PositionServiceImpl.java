package com.zhongzhou.api.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhongzhou.api.entity.Position;
import com.zhongzhou.api.mapper.PositionMapper;
import com.zhongzhou.api.service.IPositionService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 职位 服务实现类
 * </p>
 *
 * @author wqc
 * @since 2021-06-05
 */
@Service
public class PositionServiceImpl extends ServiceImpl<PositionMapper, Position> implements IPositionService {

    @Override
    public Position queryName(String name) {
        QueryWrapper<Position> wrapper = new QueryWrapper<>();
        wrapper.like("replace(name,' ','')", name.replace(" ", ""));
        List<Position> list = list(wrapper);
        if (list != null && list.size() > 0) {
            return list.get(0);
        } else {
            return null;
        }
    }
}
