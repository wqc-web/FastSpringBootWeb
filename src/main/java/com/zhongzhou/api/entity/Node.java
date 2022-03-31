package com.zhongzhou.api.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.zhongzhou.common.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * <p>
 * 敏感节点
 * </p>
 *
 * @author wqc
 * @since 2021-06-05
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_node")
@ApiModel(value = "Node对象", description = "敏感节点")
public class Node extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @TableId("id")
    private Long id;

    @ApiModelProperty(value = "节点类型：1一级安保，2二级安保，3常态安保")
    @TableField("type")
    private Integer type;

    @ApiModelProperty(value = "勤务等级：1战时（红色），2加强（黄色），3常态（正常）")
    @TableField("report_status")
    private Integer reportStatus;

    @ApiModelProperty(value = "备注状态：开始时间")
    @TableField("start_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;

    @ApiModelProperty(value = "结束时间")
    @TableField("end_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;

    @ApiModelProperty(value = "启动状态:0：未启用，1：已启用")
    @TableField("status")
    private int status;

    @ApiModelProperty(value = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @TableField("create_time")
    private LocalDateTime createTime;

    @ApiModelProperty(value = "创建者ID")
    @TableField("create_user_id")
    private Long createUserId;

    @ApiModelProperty(value = "最后一次修改时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @TableField("last_update_time")
    private LocalDateTime lastUpdateTime;

    @ApiModelProperty(value = "最后一次修改者ID")
    @TableField("last_update_user_id")
    private Long lastUpdateUserId;

    @ApiModelProperty(value = "删除标志，0：未删除，1：已删除")
    @TableField("delete_flag")
    @TableLogic
    private Integer deleteFlag;

    @ApiModelProperty(value = "版本，分布式事务标志")
    @TableField("version")
    @Version
    private Long version;
}
