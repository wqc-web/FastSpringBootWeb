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

/**
 * <p>
 * 职位
 * </p>
 *
 * @author wqc
 * @since 2021-06-05
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_position")
@ApiModel(value="Position对象", description="职位")
public class Position extends BaseEntity {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "主键")
    @TableId("id")
    private Long id;

    @ApiModelProperty(value = "名称")
    @TableField("name")
    private String name;

    @ApiModelProperty(value = "类型：1局领导，2部门领导，3其他")
    @TableField("type")
    private Integer type;

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

    @ApiModelProperty(value = "划分：1部门副职，2班子领导，3班子成员，4民警，5通信，6辅警")
    @TableField("divide")
    private Integer divide;

    @TableField("sort")
    private Long sort;

}
