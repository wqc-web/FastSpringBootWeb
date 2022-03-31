package com.zhongzhou.api.service.impl;

import com.zhongzhou.api.entity.Station;
import com.zhongzhou.api.mapper.StationMapper;
import com.zhongzhou.api.service.IStationService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 岗位 服务实现类
 * </p>
 *
 * @author wqc
 * @since 2021-06-12
 */
@Service
public class StationServiceImpl extends ServiceImpl<StationMapper, Station> implements IStationService {

}
