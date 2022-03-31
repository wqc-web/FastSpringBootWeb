package com.zhongzhou.api.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.zhongzhou.common.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * <p>
 *
 * </p>
 *
 * @author wqc
 * @since 2021-06-12
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_node_time")
@ApiModel(value = "NodeTime对象", description = "")
public class NodeTime extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @TableId("id")
    private Long id;

    @ApiModelProperty(value = "关联节点id")
    @TableField("node_id")
    private Long nodeId;

    @ApiModelProperty(value = "名称")
    @TableField("name")
    private String name;

    @ApiModelProperty(value = "月")
    @TableField("month")
    private String month;

    @ApiModelProperty(value = "日")
    @TableField("day")
    private String day;

    @ApiModelProperty(value = "备注")
    @TableField("remark")
    private String remark;

    @ApiModelProperty(value = "冗余字段")
    @TableField("field")
    private String field;

    @ApiModelProperty(value = "创建时间")
    @TableField("create_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @ApiModelProperty(value = "创建人id")
    @TableField("create_user_id")
    private Long createUserId;

    @ApiModelProperty(value = "节点类型名称")
    @TableField("type")
    private String type;

    @ApiModelProperty(value = "删除标志：0:未删除、1：已删除")
    @TableField("delete_flag")
    @TableLogic
    private String deleteFlag;

    @TableField(exist = false)
    private String time;

}
