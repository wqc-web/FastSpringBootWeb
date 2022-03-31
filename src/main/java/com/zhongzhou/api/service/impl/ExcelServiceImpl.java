package com.zhongzhou.api.service.impl;

import com.zhongzhou.api.entity.Department;
import com.zhongzhou.api.entity.Position;
import com.zhongzhou.api.entity.SysUser;
import com.zhongzhou.api.entity.UserDepartment;
import com.zhongzhou.api.service.IExcelService;
import com.zhongzhou.common.dto.UserExcelDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class ExcelServiceImpl implements IExcelService {

    @Resource
    DepartmentServiceImpl departmentService;
    @Resource
    SysUserServiceImpl sysUserService;
    @Resource
    UserDepartmentServiceImpl userDepartmentService;
    @Resource
    PositionServiceImpl positionService;


    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    public void importSysUser(List<UserExcelDTO> list) {
        if (list != null && list.size() > 0) {
            for (UserExcelDTO dto : list) {
                //用户名
                if (StringUtils.isNotBlank(dto.getRealName())) {
                    //查询指定真实姓名的用户
                    SysUser dbUser = sysUserService.queryRealName(dto.getRealName());
                    //查询指定岗位岗位
                    Position position = positionService.queryName(dto.getPositionName());
                    //岗位id
                    Long positionId = null;
                    if (position != null) {
                        positionId = position.getId();
                    }
                    //用户不存在进行添加
                    if (dbUser == null) {
                        //init user
                        SysUser sysUser = new SysUser();
                        sysUser.setCreateUserId(1L);
                        sysUser.setCreateTime(LocalDateTime.now());
                        sysUser.setUserName(dto.getRealName());
                        sysUser.setRealName(dto.getRealName());
                        sysUser.setPositionId(positionId);
                        sysUser.setPhone(dto.getPhone());
                        sysUser.setIdentityNo(dto.getIdentityNo());
                        //db add user
                        if (sysUserService.save(sysUser)) {
                            //查询部门
                            Department department = departmentService.queryLikeDepName(dto.getDepName());
                            //存在部门就进行关联
                            if (department != null) {
                                Long userId = sysUser.getId();
                                Long depId = department.getId();
                                //init UserDepartment
                                UserDepartment userDepartment = new UserDepartment();
                                userDepartment.setUserId(userId);
                                userDepartment.setDepId(depId);
                                //db add userDepartment
                                userDepartmentService.save(userDepartment);
                            }
                        } else {
                            throw new RuntimeException("用户添加失败，用户名 : " + dto.getRealName());
                        }
                    }
                }
            }
        }
    }

}
