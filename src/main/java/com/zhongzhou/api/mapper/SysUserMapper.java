package com.zhongzhou.api.mapper;

import com.zhongzhou.api.entity.SysUser;
import com.zhongzhou.common.base.BaseDao;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author wj
 * @since 2020-06-28
 */
public interface SysUserMapper extends BaseDao<SysUser> {

    /**
     * 根据角色查询用户列表
     *
     * @param roleId 角色ID
     * @return 用户列表
     */
    List<SysUser> listByRoleId(@Param("roleId") Long roleId);

    /**
     * 根据部门查询用户列表
     *
     * @param depIdList 部门ID集合
     * @return 用户列表
     */
    List<SysUser> listBydepIdList(@Param("depIdList") List<Long> depIdList);


    SysUser findDetailByUnionId(@Param("unionId") String unionId);

    /**
     * 查询指定部门的职务人员
     */
    List<SysUser> queryDepartPosition(@Param("depId") Long depId, @Param("time") String time);

    List<SysUser> queryDepartPosition(@Param("depId") Long depId);

    SysUser selectIdByName(String userName);
}
