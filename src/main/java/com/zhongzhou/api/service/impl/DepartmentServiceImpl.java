package com.zhongzhou.api.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhongzhou.api.entity.Department;
import com.zhongzhou.api.mapper.DepartmentMapper;
import com.zhongzhou.api.service.IDepartmentService;
import com.zhongzhou.common.utils.Constants;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author wj
 * @since 2020-06-28
 */
@Service
public class DepartmentServiceImpl extends ServiceImpl<DepartmentMapper, Department> implements IDepartmentService {

    private static final long serialVersionUID = 2955472032639817L;
    @Resource
    private DepartmentMapper departmentMapper;

    @Override
    public void initList() {
        List<Department> departmentList = list();
        departmentList.forEach(department -> {
            Constants.MAP_DEPARTMENT.put(department.getId(), department.getDepName());
        });
    }

    @Override
    public List<Department> findBySysUserId(Long sysUserId) {
        return departmentMapper.findBySysUserId(sysUserId);
    }

    @Override
    public List<Department> findDepartmentTree() {
        QueryWrapper<Department> wrapper = new QueryWrapper<>();
        wrapper.eq("parent_id", 0);
        List<Department> parentDepartmentList = list(wrapper);
        return findDepartmentChildrenList(parentDepartmentList);
    }

    @Override
    public Department queryLikeDepName(String name) {
        QueryWrapper<Department> wrapper = new QueryWrapper<>();
        wrapper.like("replace(dep_name,' ','')", name.replace(" ", ""));
        List<Department> list = list(wrapper);
        if(list != null && list.size() > 0){
            return list.get(0);
        }else{
            return null;
        }
    }

    @Override
    public List<Department> queryType(Integer type) {
        QueryWrapper<Department> wrapper = new QueryWrapper<>();
        wrapper.eq("type", type);
        return list(wrapper);
    }

    /**
     * 递归查询部门树
     *
     * @param parentDepartmentList 父级部门信息
     * @return 部门树
     */
    private List<Department> findDepartmentChildrenList(List<Department> parentDepartmentList) {
        if (null != parentDepartmentList && parentDepartmentList.size() > 0) {
            parentDepartmentList.forEach(parent -> {
                QueryWrapper<Department> wrapper = new QueryWrapper<>();
                wrapper.eq("parent_id", parent.getId());
                List<Department> departmentList = list(wrapper);
                if (null != departmentList && departmentList.size() > 0) {
                    parent.setChildren(departmentList);
                    findDepartmentChildrenList(departmentList);
                }
            });
        }
        return parentDepartmentList;
    }
}
