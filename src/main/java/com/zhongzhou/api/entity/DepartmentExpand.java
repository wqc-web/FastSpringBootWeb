package com.zhongzhou.api.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
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
 * 
 * </p>
 *
 * @author wqc
 * @since 2021-06-16
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_department_expand")
@ApiModel(value="DepartmentExpand对象", description="")
public class DepartmentExpand extends BaseEntity {

    private static final long serialVersionUID=1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @TableField("name")
    private String name;

    @ApiModelProperty(value = "部门id")
    @TableField("dep_id")
    private Integer depId;


}
