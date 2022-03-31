package com.zhongzhou.api.service;

import com.zhongzhou.common.dto.UserExcelDTO;

import java.util.List;

/**
 * excel
 */
public interface IExcelService {

    /**
     * 导入用户
     *
     * @param list       用户集合
     */
    void importSysUser(List<UserExcelDTO> list);

}
