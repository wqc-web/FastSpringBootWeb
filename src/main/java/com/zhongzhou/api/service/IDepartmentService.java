package com.zhongzhou.api.service;

import com.zhongzhou.api.entity.Department;
import com.zhongzhou.common.base.BaseService;

import java.util.List;

/**
 * <p>
 * 服务类--部门
 * </p>
 *
 * @author wj
 * @since 2020-06-28
 */
public interface IDepartmentService extends BaseService<Department> {
    /**
     * 初始化列表
     */
    void initList();

    /**
     * 查询用户部门信息
     *
     * @param sysUserId 用户ID
     * @return 部门详情
     */
    List<Department> findBySysUserId(Long sysUserId);

    /**
     * 获取部门树
     *
     * @return 部门列表
     */
    List<Department> findDepartmentTree();

    /**
     * 查询对应名称的部门
     *
     * @param name 部门名称
     * @return 部门
     */
    Department queryLikeDepName(String name);

    /**
     * 根据类型查询
     *
     * @param type 类型
     * @return 部门集合
     */
    List<Department> queryType(Integer type);
}
