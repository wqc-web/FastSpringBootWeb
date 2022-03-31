package com.zhongzhou.api.mapper;

import com.zhongzhou.api.entity.Node;
import com.zhongzhou.common.base.BaseDao;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 敏感节点 Mapper 接口
 * </p>
 *
 * @author wqc
 * @since 2021-06-05
 */
public interface NodeMapper extends BaseDao<Node> {

    Node queryRuleByTime(@Param("time") String time);

    /**
     * 日期节点列表
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return 节点列表
     */
    List<Node> dateNodeList(@Param("startTime") String startTime, @Param("endTime") String endTime);
}
