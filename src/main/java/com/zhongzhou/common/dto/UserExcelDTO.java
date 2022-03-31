package com.zhongzhou.common.dto;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;

/**
 * 用户导入DTO
 */
@Data
public class UserExcelDTO {
    /**
     * 姓名
     */
    @Excel(name = "姓名")
    private String realName;
    /**
     * 身份证
     */
    @Excel(name = "身份证号码")
    private String identityNo;
    /**
     * 电话
     */
    @Excel(name = "电话号码")
    private String phone;
    /**
     * 部门名称
     */
    @Excel(name = "部门")
    private String depName;

    /**
     * 岗位名称
     */
    @Excel(name = "岗位")
    private String positionName;

}
