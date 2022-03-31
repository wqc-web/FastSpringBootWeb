package com.zhongzhou.api.service;

import com.zhongzhou.common.vo.NodeMarkVO;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 移动端 服务类
 * </p>
 *
 * @author wqc
 * @since 2021-06-05
 */
public interface IMobileService {
    /**
     * 日期节点列表
     *
     * @param time yyyy-MM
     * @return 节点标记列表
     */
    List<NodeMarkVO> dateNodeList(String time);
    /**
     * 人员列表
     *
     * @param time yyyy-MM-dd
     * @return 数据
     */
    Map<String, Object> personList(String time);
}
