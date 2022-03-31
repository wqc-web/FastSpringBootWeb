package com.zhongzhou.api.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.Version;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.zhongzhou.common.base.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * <p>
 * 勤务报备对调
 * </p>
 *
 * @author wqc
 * @since 2021-06-05
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_report_swap")
@ApiModel(value="ReportSwap对象", description="勤务报备对调")
public class ReportSwap extends BaseEntity {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "主键")
    @TableId("id")
    private Long id;

    @ApiModelProperty(value = "交换状态：0未处理，1同意，2拒绝")
    @TableField("status")
    private Integer status;

    @ApiModelProperty(value = "备注内容")
    @TableField("comment")
    private String comment;

    @ApiModelProperty(value = "延时（单位分钟）")
    @TableField("delay_time")
    private Integer delayTime;

    @ApiModelProperty(value = "接收者ID")
    @TableField("receive_user_id")
    private Long receiveUserId;

    @TableField(exist = false)
    private String receiveRealName;

    @ApiModelProperty(value = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @TableField("create_time")
    private LocalDateTime createTime;

    @ApiModelProperty(value = "创建者ID")
    @TableField("create_user_id")
    private Long createUserId;

    @TableField(exist = false)
    private String createRealName;

    @ApiModelProperty(value = "最后一次修改时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @TableField("last_update_time")
    private LocalDateTime lastUpdateTime;

    @ApiModelProperty(value = "最后一次修改者ID")
    @TableField("last_update_user_id")
    private Long lastUpdateUserId;

    @TableField(exist = false)
    private String lastUpdateUserName;

    @ApiModelProperty(value = "删除标志，0：未删除，1：已删除")
    @TableField("delete_flag")
    @TableLogic
    private Integer deleteFlag;

    @ApiModelProperty(value = "版本，分布式事务标志")
    @TableField("version")
    @Version
    private Long version;

    @ApiModelProperty(value = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("application_time")
    private LocalDateTime applicationTime;


    @ApiModelProperty(value = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @TableField("swap_time")
    private String swapTime;

    @TableField(exist = false)
    private Integer operation;


    @TableField(exist = false)
    private Integer mode;
    @TableField(exist = false)
    private Integer leaderMode;
    @TableField(exist = false)
    private Integer DeLeaderMode;


}
