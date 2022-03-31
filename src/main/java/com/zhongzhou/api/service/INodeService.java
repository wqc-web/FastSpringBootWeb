package com.zhongzhou.api.service;

import com.zhongzhou.api.entity.Node;
import com.zhongzhou.common.base.BaseService;

import java.util.Map;

/**
 * <p>
 * 敏感节点 服务类
 * </p>
 *
 * @author wqc
 * @since 2021-06-05
 */
public interface INodeService extends BaseService<Node> {

    /**
     * 保存节点
     *
     * @param node
     * @return
     */
    boolean saveNode(Node node);

    boolean updateNodeById(Node node);

    Map<String, Integer> queryRuleByTime(String time);

    Integer updateStatus(Node node);
}
