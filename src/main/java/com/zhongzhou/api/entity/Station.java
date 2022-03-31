package com.zhongzhou.api.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.Version;
import com.baomidou.mybatisplus.annotation.TableId;
import com.zhongzhou.common.base.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 岗位
 * </p>
 *
 * @author wqc
 * @since 2021-06-12
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_station")
@ApiModel(value="Station对象", description="岗位")
public class Station extends BaseEntity {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "主键")
    @TableId("id")
    private Long id;

    @ApiModelProperty(value = "名称")
    @TableField("name")
    private String name;

    @ApiModelProperty(value = "排序")
    @TableField("sort")
    private Long sort;


}
