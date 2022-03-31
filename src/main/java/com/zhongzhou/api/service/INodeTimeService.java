package com.zhongzhou.api.service;

import com.zhongzhou.api.entity.NodeTime;
import com.zhongzhou.common.base.BaseService;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author wqc
 * @since 2021-06-12
 */
public interface INodeTimeService extends BaseService<NodeTime> {

    boolean saveNodeTime(NodeTime nodeTime);

    boolean updateNodeTimeById(NodeTime nodeTime);
}
