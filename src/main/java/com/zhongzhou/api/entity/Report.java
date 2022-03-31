package com.zhongzhou.api.entity;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.zhongzhou.common.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 勤务报备
 * </p>
 *
 * @author wqc
 * @since 2021-06-05
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_report")
@ApiModel(value = "Report对象", description = "勤务报备")
public class Report extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @TableId("id")
    private Long id;

    @ApiModelProperty(value = "饮酒状态：0禁止，1底，2中，3高")
    @TableField("drink_wine_status")
    private Integer drinkWineStatus;

    @ApiModelProperty(value = "工作状态：1坚守岗位，2不得离洪，3不得离淮，4因公离淮，5因私离淮")
    @TableField("work_status")
    private Integer workStatus;

    @ApiModelProperty(value = "备注状态：1回家，2应酬，3出差")
    @TableField("comment_status")
    private Integer commentStatus;

    @ApiModelProperty(value = "备注内容")
    @TableField("comment_content")
    private String commentContent;

    @ApiModelProperty(value = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @TableField("create_time")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
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


    @TableField("username")
    @Excel(name = "用户名")
    private String userName;

    @TableField("dep_id")
    private Long depId;

    @TableField(exist = false)
    private String depName;

    @TableField("position_id")
    private Long positionId;

    /**
     * 职位名称
     */
    @TableField(exist = false)
    private String positionName;

    /**
     * 职位排序
     */
    @TableField(exist = false)
    private Long positionSort;

    /**
     * 用户号码
     */
    @TableField(exist = false)
    private String userPhone;

    /**
     * 部门集合
     */
    @TableField(exist = false)
    private List<Department> departmentList;

    @TableField(exist = false)
    private String poisitionName;
}
