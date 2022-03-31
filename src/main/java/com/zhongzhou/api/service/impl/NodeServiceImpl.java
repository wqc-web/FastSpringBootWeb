package com.zhongzhou.api.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhongzhou.api.entity.Node;
import com.zhongzhou.api.entity.Report;
import com.zhongzhou.api.mapper.NodeMapper;
import com.zhongzhou.api.mapper.ReportMapper;
import com.zhongzhou.api.service.INodeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 敏感节点 服务实现类
 * </p>
 *
 * @author wqc
 * @since 2021-06-05
 */
@Service
public class NodeServiceImpl extends ServiceImpl<NodeMapper, Node> implements INodeService {

    @Resource
    NodeMapper nodeMapper;
    @Resource
    ReportMapper reportMapper;
    @Resource
    ReportServiceImpl reportService;

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    @Override
    public boolean saveNode(Node node) {
        //角色权限判断(前端判) TODO

        //判断时间是否重叠
        LocalDate startTime = node.getStartTime().toLocalDate();

        LocalDate endTime = node.getEndTime().toLocalDate();

        List<Node> nodeList = nodeMapper.selectList(null);
        if (nodeList != null && nodeList.size() > 0) {
            for (Node node1 : nodeList) {
                LocalDate startTime1 = node1.getStartTime().toLocalDate();
                LocalDate endTime1 = node1.getEndTime().toLocalDate();
                //在库里时间段之内就跳出循环
                //通过开始最大点和结束最小点是否有交集判断是否重叠
                LocalDate startPoint = startTime.compareTo(startTime1) < 0 ? startTime1 : startTime;
                LocalDate endPoint = endTime.compareTo(endTime1) < 0 ? endTime : endTime1;
                if (startPoint.compareTo(endPoint) <= 0) {
                    //说明重复
                    //throw new RuntimeException("提供的时间段存在重叠");
                    return  false;
                }
            }
        }

        //添加节点
        node.setCreateTime(LocalDateTime.now());
        node.setStatus(1);
        node.setType(node.getReportStatus());
        int insert = nodeMapper.insert(node);
        //根据规则修改该时间段的报备人的状态
        List<Report> reportList = reportMapper.selectReportList(node.getStartTime(), node.getEndTime());
        if (!CollectionUtils.isEmpty(reportList)) {
            updateWorkStatus(node.getReportStatus(), reportList);
        }


        return true;
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    @Override
    public boolean updateNodeById(Node node) {
        //判断时间是否重叠
        LocalDate startTime = node.getStartTime().toLocalDate();

        LocalDate endTime = node.getEndTime().toLocalDate();

        QueryWrapper<Node> wrapper = new QueryWrapper<>();
        wrapper.ne("id", node.getId());
        List<Node> nodeList = nodeMapper.selectList(wrapper);

        if (nodeList != null && nodeList.size() > 0) {
            for (Node node1 : nodeList) {
                LocalDate startTime1 = node1.getStartTime().toLocalDate();
                LocalDate endTime1 = node1.getEndTime().toLocalDate();
                //在库里时间段之内就跳出循环
                //通过开始最大点和结束最小点是否有交集判断是否重叠
                LocalDate startPoint = startTime.compareTo(startTime1) < 0 ? startTime1 : startTime;
                LocalDate endPoint = endTime.compareTo(endTime1) < 0 ? endTime : endTime1;
                if (startPoint.compareTo(endPoint) <= 0) {
                    //说明重复
                    throw new RuntimeException("提供的时间段存在重叠");
                }
            }
        }
        //修改节点
        node.setLastUpdateTime(LocalDateTime.now());
        if (node.getReportStatus() != null) {
            node.setType(node.getReportStatus());
        }
        nodeMapper.updateById(node);

        //根据规则修改该时间段的报备人的状态
        List<Report> reportList = reportMapper.selectReportList(node.getStartTime(), node.getEndTime());
        if (!CollectionUtils.isEmpty(reportList)) {
            updateWorkStatus(node.getReportStatus(), reportList);
        }

        return true;
    }

    /**
     * 根据时间返回规则
     *
     * @param time 时间
     * @return 规则
     */
    @Override
    public Map<String, Integer> queryRuleByTime(String time) {
        Node node = nodeMapper.queryRuleByTime(time);
        Map<String, Integer> ruleMap = new HashMap<>();
        if (node == null) {
            ruleMap.put("drink_wine_status", 1);
            ruleMap.put("work_status", 3);
        } else {
            switch (node.getReportStatus()) {
                case 3:
                    //正常
                    ruleMap.put("drink_wine_status", 1);
                    ruleMap.put("work_status", 3);
                    break;
                case 2:
                    //加强
                    ruleMap.put("drink_wine_status", 0);
                    ruleMap.put("work_status", 2);
                    break;
                case 1:
                    //战时
                    ruleMap.put("drink_wine_status", 0);
                    ruleMap.put("work_status", 1);
                    break;
            }
        }
        return ruleMap;
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    @Override
    public Integer updateStatus(Node node) {
        //修改状态
        UpdateWrapper<Node> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", node.getId());
        updateWrapper.set("status", node.getStatus());

        return nodeMapper.update(null, updateWrapper);
    }

    /**
     * 根据勤务等级更新用户报备状态
     *
     * @param reportStatus 勤务等级：1战时（红色），2加强（黄色），3常态（正常）
     * @param reportList   报备数据
     */
    private void updateWorkStatus(Integer reportStatus, List<Report> reportList) {
        for (Report report : reportList) {
            switch (reportStatus) {
                case 1:
                    report.setDrinkWineStatus(0);//饮酒状态:禁止
                    report.setWorkStatus(1);//工作状态:坚守岗位
                    break;
                case 2:
                    report.setDrinkWineStatus(0);//禁止
                    report.setWorkStatus(2);//不得离洪
                    break;
                case 3:
                    report.setDrinkWineStatus(1);//低
                    report.setWorkStatus(3);//不得离淮
                    break;
            }
        }
        //修改
        reportService.updateBatchById(reportList);
    }
}
