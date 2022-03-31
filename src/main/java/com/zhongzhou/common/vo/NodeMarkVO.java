package com.zhongzhou.common.vo;

import com.zhongzhou.api.entity.NodeTime;
import lombok.Data;

import java.util.List;

/**
 * 节点标记VO
 */
@Data
public class NodeMarkVO {
    /**
     * 标记时间 yyyy-MM-dd
     */
    private String time;
    /**
     * 标记内容
     */
    private String text;
    /**
     * 是否显示标记点
     */
    private Boolean markPoint;
    /**
     * 标记内容颜色
     */
    private String markTextColor;
    /**
     * 左上角标记点内容
     */
    private String pointText;
    /**
     * 标记点颜色
     */
    private String pointTextColor;
    /**
     * 勤务等级：1战时（红色），2加强（黄色），3常态（正常）
     */
    private Integer reportStatus;
    /**
     * 开始时间
     */
    private String startTime;
    /**
     * 结束时间
     */
    private String endTime;
    /**
     * 节点时间集合
     */
    private List<NodeTime> nodeTimeList;
}
