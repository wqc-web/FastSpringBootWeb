package com.zhongzhou.api.service;

import com.zhongzhou.api.entity.Position;
import com.zhongzhou.common.base.BaseService;

/**
 * <p>
 * 职位 服务类
 * </p>
 *
 * @author wqc
 * @since 2021-06-05
 */
public interface IPositionService extends BaseService<Position> {

    /**
     * 根据名称查询
     *
     * @param name 名称
     * @return 职位
     */
    Position queryName(String name);

}
