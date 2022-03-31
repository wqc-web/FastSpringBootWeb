package com.zhongzhou.api.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zhongzhou.api.entity.SysUser;
import com.zhongzhou.common.base.BaseService;
import com.zhongzhou.common.base.Pager;

import java.util.List;

/**
 * <p>
 * 服务类--用户
 * </p>
 *
 * @author wj
 * @since 2020-06-28
 */
public interface ISysUserService extends BaseService<SysUser> {

    /**
     * 分页列表
     *
     * @param pager   分页参数
     * @param wrapper 查询条件
     * @return 用户列表
     */
    List<SysUser> pageSysUserList(Pager<SysUser> pager, QueryWrapper<SysUser> wrapper);

    /**
     * 详情
     *
     * @param id 用户ID
     * @return 用户详情
     */
    SysUser findDetailById(Long id);

    /**
     * 新增系统用户
     *
     * @param sysUser 系统用户实体类
     * @return true成功，false失败
     */
    boolean saveSysUser(SysUser sysUser);

    /**
     * 编辑系统用户信息
     *
     * @param sysUser 系统用户实体类
     * @return true成功，false失败
     */
    boolean updateUserById(SysUser sysUser);

    /**
     * 根据角色查询用户列表
     *
     * @param roleId 角色ID
     * @return 用户列表
     */
    List<SysUser> listByRoleId(Long roleId);

    /**
     * 根据部门查询用户列表
     *
     * @param depIdList 部门ID集合
     * @return 用户列表
     */
    List<SysUser> listByDepIdList(List<Long> depIdList);

    /**
     * 根据用户名添加数据
     *
     * @param realName 真实姓名
     * @return 用户
     */
    SysUser queryRealName(String realName);

    /**
     * 查询指定部门的职务人员
     *
     * @param depId 部门id
     * @param time  yyyy-MM-dd
     * @return 用户集合
     */
    List<SysUser> queryDepartPosition(Long depId, String time);
}
