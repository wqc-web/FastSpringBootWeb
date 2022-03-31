package com.zhongzhou.api.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhongzhou.api.entity.NodeTime;
import com.zhongzhou.api.mapper.NodeTimeMapper;
import com.zhongzhou.api.service.INodeTimeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author wqc
 * @since 2021-06-12
 */
@Service
public class NodeTimeServiceImpl extends ServiceImpl<NodeTimeMapper, NodeTime> implements INodeTimeService {

    @Autowired
    private NodeTimeServiceImpl nodeTimeService;

    @Override
    public boolean saveNodeTime(NodeTime nodeTime) {
        nodeTime.setTime(nodeTime.getMonth() + "-" + nodeTime.getDay());
        List<NodeTime> nodeTimeList = nodeTimeService.list();
        String time = nodeTime.getTime();
//        for (NodeTime nd : nodeTimeList) {
//            String time1 = nd.getTime();
//            if (time.equals(time1)) {
//                return false;
//            }
//        }

        nodeTime.setCreateTime(LocalDateTime.now());
        nodeTime.setDeleteFlag("0");

        if (Integer.parseInt(nodeTime.getMonth()) < 10) {
            nodeTime.setMonth("0" + nodeTime.getMonth());
        }

        if (Integer.parseInt(nodeTime.getDay()) < 10) {
            nodeTime.setDay("0" + nodeTime.getDay());
        }
        return nodeTimeService.save(nodeTime);
    }

    @Override
    public boolean updateNodeTimeById(NodeTime nodeTime) {
        nodeTime.setTime(nodeTime.getMonth() + "-" + nodeTime.getDay());
        QueryWrapper<NodeTime> wrapper = new QueryWrapper<>();
        wrapper.ne("id", nodeTime.getId());
        List<NodeTime> nodeTimeList = nodeTimeService.list(wrapper);
        String time = nodeTime.getTime();
//        for (NodeTime nd : nodeTimeList) {
//            String ndTime = nd.getTime();
//            if (time.equals(ndTime)) {
//                return false;
//            }
//        }

        nodeTime.setCreateTime(LocalDateTime.now());
        nodeTime.setDeleteFlag("0");
        if (Integer.parseInt(nodeTime.getMonth()) < 10) {
            nodeTime.setMonth("0" + nodeTime.getMonth());
        }
        if (Integer.parseInt(nodeTime.getDay()) < 10) {
            nodeTime.setDay("0" + nodeTime.getDay());
        }
        return nodeTimeService.updateById(nodeTime);
    }


}
