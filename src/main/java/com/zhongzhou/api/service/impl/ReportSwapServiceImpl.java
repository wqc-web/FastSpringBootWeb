package com.zhongzhou.api.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhongzhou.api.entity.*;
import com.zhongzhou.api.mapper.*;
import com.zhongzhou.api.service.IReportSwapService;
import com.zhongzhou.api.service.IWxLoginService;
import com.zhongzhou.common.base.Pager;
import com.zhongzhou.common.utils.DateUtil;
import org.apache.commons.lang3.time.DateUtils;
import org.hibernate.validator.constraints.Length;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 勤务报备对调 服务实现类
 * </p>
 *
 * @author wqc
 * @since 2021-06-05
 */
@Service
public class ReportSwapServiceImpl extends ServiceImpl<ReportSwapMapper, ReportSwap> implements IReportSwapService {

    @Resource
    private ReportSwapMapper reportSwapMapper;
    @Resource
    private DepartmentMapper departmentMapper;
    @Resource
    private UserDepartmentMapper userDepartmentMapper;
    @Resource
    private SysUserMapper sysUserMapper;
    @Resource
    private ReportMapper reportMapper;
    @Resource
    private ReportServiceImpl reportService;
    @Resource
    private UserRoleMapper userRoleMapper;
    @Resource
    private SysUserServiceImpl sysUserService;
    @Resource
    private IWxLoginService iWxLoginService;


    @Override
    public List<ReportSwap> queryPage(Pager<ReportSwap> pager, ReportSwap reportSwap) {
        Long startPage = null;
        Long size = null;
        if (pager != null) {
            size = pager.getSize();
            startPage = (pager.getCurrent() - 1) * size;
        }
        return reportSwapMapper.queryPage(startPage, size, reportSwap);
    }

    @Override
    public Integer queryCount(ReportSwap reportSwap) {
        return reportSwapMapper.queryCount(reportSwap);
    }

    @Override
    public List<SysUser> selectDepPepByPreId(Long id, String selfTime, String swapTime) {
        //查询用户详情
        SysUser user = sysUserService.findDetailById(id);
        //如果申请人是辅警
        if (user.getPositionId() == 130) {
            if (user != null && user.getDepartmentList() != null && user.getDepartmentList().size() > 0) {
                //dataList
                List<SysUser> dataList = new ArrayList<>();
                //部门id集合
                List<Long> depIdList = user.getDepartmentList().stream().map(Department::getId).collect(Collectors.toList());
                sysUserService.listByDepIdList(depIdList).forEach(transferUser -> {
                    //登录用户和存在指定报备用户 跳过
                    if (!transferUser.getId().equals(user.getId()) && existTimeReportUser(transferUser.getId(), swapTime) && !existTimeReportUser(transferUser.getId(), selfTime)) {
                        if (transferUser.getPositionId() == 130) {
                            dataList.add(transferUser);
                        }
                    }
                });
                return dataList;
            } else {
                return new ArrayList<>();
            }
        }
        //如果申请人是民警
        if (user.getPositionId() == 128) {
            if (user != null && user.getDepartmentList() != null && user.getDepartmentList().size() > 0) {
                //dataList
                List<SysUser> dataList = new ArrayList<>();
                //部门id集合
                List<Long> depIdList = user.getDepartmentList().stream().map(Department::getId).collect(Collectors.toList());
                sysUserService.listByDepIdList(depIdList).forEach(transferUser -> {
                    //登录用户和存在指定报备用户 跳过
                    if (!transferUser.getId().equals(user.getId()) && existTimeReportUser(transferUser.getId(), swapTime) && !existTimeReportUser(transferUser.getId(), selfTime)) {
                        if (transferUser.getPositionId() == 128) {
                            dataList.add(transferUser);
                        }
                    }
                });
                return dataList;
            } else {
                return new ArrayList<>();
            }
        }
        //如果申请人是局领导
        if (user.getPositionId() == 125) {
            List<SysUser> dataList = new ArrayList<>();
            DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime createTime = LocalDateTime.parse(swapTime + " 00:00:00", df);
            System.out.println(createTime);
            QueryWrapper<Report> q = new QueryWrapper<>();
            q.eq("create_time", createTime);
            q.eq("dep_id", 10001);
            List<Report> reports = reportMapper.selectList(q);
            for (Report report : reports) {
                System.out.println(report.getUserName());
                SysUser sysUser = sysUserMapper.selectIdByName(report.getUserName());
                if (sysUser != null) {
                    if (sysUser.getPositionId() == 125) {
                        dataList.add(sysUser);
                    }
                }

            }
            return dataList;
        }
        //如果申请人是部门领导
        if (user.getPositionId() == 126) {
            if (user != null && user.getDepartmentList() != null && user.getDepartmentList().size() > 0) {
                //dataList
                List<SysUser> dataList = new ArrayList<>();
                //部门id集合
                List<Long> depIdList = user.getDepartmentList().stream().map(Department::getId).collect(Collectors.toList());
                sysUserService.listByDepIdList(depIdList).forEach(transferUser -> {
                    //登录用户和存在指定报备用户 跳过
                    if (!transferUser.getId().equals(user.getId()) && existTimeReportUser(transferUser.getId(), swapTime) && !existTimeReportUser(transferUser.getId(), selfTime)) {
                        if (transferUser.getPositionId() == 128) {
                            dataList.add(transferUser);
                        }
                    }
                });
                return dataList;
            } else {
                return new ArrayList<>();
            }
        } else {
            if (user != null && user.getDepartmentList() != null && user.getDepartmentList().size() > 0) {
                //dataList
                List<SysUser> dataList = new ArrayList<>();
                //部门id集合
                List<Long> depIdList = user.getDepartmentList().stream().map(Department::getId).collect(Collectors.toList());
                sysUserService.listByDepIdList(depIdList).forEach(transferUser -> {
                    //登录用户和存在指定报备用户 跳过
                    if (!transferUser.getId().equals(user.getId()) && existTimeReportUser(transferUser.getId(), swapTime) && !existTimeReportUser(transferUser.getId(), selfTime)) {
                        dataList.add(transferUser);
                    }
                });
                return dataList;
            } else {
                return new ArrayList<>();
            }
        }

    }

    /**
     * 指定用户和时间报备是否存在
     *
     * @param userId 用户id
     * @param time   yyyy-MM-dd
     * @return true存在 false不存在
     */
    private boolean existTimeReportUser(Long userId, String time) {
        QueryWrapper<Report> wrapper = new QueryWrapper<>();
        wrapper.eq("DATE_FORMAT(create_time,'%Y-%m-%d')", time);
        wrapper.eq("create_user_id", userId);
        return reportService.count(wrapper) > 0;
    }

    @Override
    public Integer operationReport(Long id, Integer operation) {
        ReportSwap reportSwap = reportSwapMapper.selectById(id);
        reportSwap.setStatus(operation);
        SysUser sysUser = sysUserService.findDetailById(reportSwap.getCreateUserId());
        if (operation == 4) {
            String swapTime = reportSwap.getSwapTime();
            Long createUserId = reportSwap.getCreateUserId();
            Long receiveUserId = reportSwap.getReceiveUserId();
            QueryWrapper<Report> wrapper = new QueryWrapper<>();
            wrapper.eq("create_time", reportSwap.getApplicationTime()).eq("create_user_id", createUserId);
            List<Report> reportList = reportMapper.selectList(wrapper);
            Report report = reportList.get(0);
            LocalDateTime createTime1 = report.getCreateTime();
            QueryWrapper<Report> wrapper1 = new QueryWrapper<>();
            wrapper1.eq("create_time", swapTime).eq("create_user_id", receiveUserId);
            List<Report> reportList1 = reportMapper.selectList(wrapper1);
            Report report1 = reportList1.get(0);
          //  LocalDateTime createTime2 = report1.getCreateTime();
           // report.setCreateTime(createTime2);
            Long userId = report.getCreateUserId();
            Long userId1 = report1.getCreateUserId();
            report.setCreateUserId(userId1);
            report1.setCreateUserId(userId);
             String userName = sysUserService.findDetailById(userId1).getUserName();
             String userName1 = sysUserService.findDetailById(userId).getUserName();
            report.setUserName(userName);
            report1.setUserName(userName1);
          //  report1.setCreateTime(createTime1);
            reportMapper.updateById(report1);
            reportMapper.updateById(report);
            int i = reportSwapMapper.updateById(reportSwap);
            iWxLoginService.sendTpMessage(sysUserService.findDetailById(reportSwap.getCreateUserId()).getUnionId(), "您和" + sysUserService.findDetailById(reportSwap.getReceiveUserId()).getRealName() + "备勤日期已调换", reportSwap.getId(), 0L);
            iWxLoginService.sendTpMessage(sysUserService.findDetailById(reportSwap.getReceiveUserId()).getUnionId(), "您和" + sysUserService.findDetailById(reportSwap.getCreateUserId()).getRealName() + "备勤调日期已调换", reportSwap.getId(), 0L);
            //如果申请人是局领导
            if (sysUser.getPositionId() == 125) {
                //发送消息给局长和政委
                iWxLoginService.sendTpMessage("15195384123", sysUserService.findDetailById(reportSwap.getCreateUserId()).getRealName() + "和" + sysUserService.findDetailById(reportSwap.getReceiveUserId()).getRealName() + "备勤日期已调换", reportSwap.getId(), 1L);
                iWxLoginService.sendTpMessage("087003", sysUserService.findDetailById(reportSwap.getCreateUserId()).getRealName() + "和" + sysUserService.findDetailById(reportSwap.getCreateUserId()).getRealName() + "备勤日期已调换", reportSwap.getId(), 1L);
                //如果申请人是附近
            } else if (sysUser.getPositionId() == 130) {
                QueryWrapper<UserDepartment> queryWrapper = new QueryWrapper<>();
                //查询申请人的部门id
                queryWrapper.eq("user_id", createUserId);
                List<UserDepartment> userDepartments = userDepartmentMapper.selectList(queryWrapper);
                //查询出所在部门id
                Long depId = userDepartments.get(0).getDepId();
                //判断所属部门 交警大队 刑警大队  指挥中心 是不是所属这三个部门中
                if (depId == 10002 || depId == 10019 || depId == 10014 || depId == 1404352904213389314L) {
                    //如果申请人是这三个部门的 就去查询这个三个部门中的民警,并发送消息
                    QueryWrapper<UserDepartment> queryWrapper1 = new QueryWrapper<>();
                    queryWrapper1.eq("dep_id", depId);
                    //查询出该部门所有人员
                    List<UserDepartment> departments = userDepartmentMapper.selectList(queryWrapper1);
                    //遍历或者人员职位id
                    for (UserDepartment department : departments) {
                        SysUser user = sysUserService.findDetailById(department.getUserId());
                        //判断 职位民警就发送消息
                        if (user.getPositionId() == 128 && user.getDeId().equals(user.getDeId())) {
                            iWxLoginService.sendTpMessage(user.getUnionId(), sysUserService.findDetailById(reportSwap.getCreateUserId()).getRealName() + "和" + sysUserService.findDetailById(reportSwap.getReceiveUserId()).getRealName() + "备勤日期已调换", reportSwap.getId(), 1L);
                        }
                        //通知当事人
                    }
                }
            }else  if (sysUser.getPositionId()==128){
                //查找教导员和部门领导 发送消息
                QueryWrapper<UserDepartment> queryWrapper = new QueryWrapper<>();
                //查询申请人的部门id
                queryWrapper.eq("user_id", createUserId);
                List<UserDepartment> userDepartments = userDepartmentMapper.selectList(queryWrapper);
                //查询出所在部门id
                Long depId = userDepartments.get(0).getDepId();
                QueryWrapper<UserDepartment> queryWrapper1 = new QueryWrapper<>();
                queryWrapper1.eq("dep_id", depId);
                List<UserDepartment> departments = userDepartmentMapper.selectList(queryWrapper1);
                for (UserDepartment department : departments) {
                    //根据关联的人员查询出人员信息 判断等于职位是部门领导的
                    SysUser detailById = sysUserService.findDetailById(department.getUserId());
                    //如果是部门领导和教导员 同时发送消息
                    if (detailById.getPositionId() == 126 || detailById.getPositionId() == 137  ) {
                        //给部门领导发消息
                        iWxLoginService.sendTpMessage(sysUserService.findDetailById(department.getUserId()).getUnionId(), sysUserService.findDetailById(reportSwap.getCreateUserId()).getRealName() + "和" + sysUserService.findDetailById(reportSwap.getReceiveUserId()).getRealName() + "备勤日期已调换", reportSwap.getId(), 1L);
                    }
                }

            }else if (sysUser.getPositionId()==126){
                QueryWrapper<UserDepartment> queryWrapper = new QueryWrapper<>();
                //查询申请人的部门id
                queryWrapper.eq("user_id", createUserId);
                List<UserDepartment> userDepartments = userDepartmentMapper.selectList(queryWrapper);
                //查询出所在部门id
                Long depId = userDepartments.get(0).getDepId();
                Department department1 = departmentMapper.selectById(depId);
                Long ldeaderId = department1.getLeaderId();
                SysUser detailById = sysUserService.findDetailById(ldeaderId);
                iWxLoginService.sendTpMessage(detailById.getUnionId(), sysUserService.findDetailById(reportSwap.getCreateUserId()).getRealName() + "和" + sysUserService.findDetailById(reportSwap.getReceiveUserId()).getRealName() + "备勤日期已调换", reportSwap.getId(), 1L);

            }

        }else {
                reportSwapMapper.updateById(reportSwap);
                iWxLoginService.sendTpMessage(sysUserService.findDetailById(reportSwap.getCreateUserId()).getUnionId(),
                        "您和" + sysUserService.findDetailById(reportSwap.getReceiveUserId()).getRealName() + "您有备勤调换申请审核被拒绝,请重新选择提交", reportSwap.getId(), 0L);
                iWxLoginService.sendTpMessage(sysUserService.findDetailById(reportSwap.getReceiveUserId()).getUnionId(),
                        "您和" + sysUserService.findDetailById(reportSwap.getCreateUserId()).getRealName() + "您有备勤调换申请审核被拒绝,请重新选择提交", reportSwap.getId(), 0L);
            }


        return 1;
    }

    @Override
    public ReportSwap messagePush(Long userId) {
        ReportSwap reportSwap = reportSwapMapper.messagePush(userId);
        if (reportSwap != null) {
            reportSwap.setCreateRealName(sysUserMapper.selectById(reportSwap.getCreateUserId()).getRealName());
            LocalDateTime applicationTime = reportSwap.getCreateTime();
            LocalDateTime now = LocalDateTime.now();
            java.time.Duration duration = java.time.Duration.between(applicationTime, now);
            long minutes = duration.toMinutes();
            System.out.println(minutes);
            if (minutes > 3) {
                reportSwap.setStatus(3);
                reportSwapMapper.updateById(reportSwap);
            }
        }
        return reportSwap;
    }


    public Integer checkReport(Long id, Integer operation) {
        ReportSwap reportSwap = reportSwapMapper.selectById(id);
        reportSwap.setStatus(operation);
        //如果同意
        if (operation == 1) {
            //根据申请人的部门id
            Long createUserId = reportSwap.getCreateUserId();
            //如果申请人为局领导 直接通过 不需要审核
            if (sysUserService.findDetailById(createUserId).getPositionId() == 125) {
                //如果同意
                if (operation == 1) {
                    //直接改为4 为领导通过状态 不需要领导审核
                    reportSwap.setStatus(4);
                    //交换时间
                    String swapTime = reportSwap.getSwapTime();
                    //申请人id
                    Long userId = reportSwap.getCreateUserId();
                    //交换人id
                    Long receiveUserId = reportSwap.getReceiveUserId();
                    QueryWrapper<Report> wrapper = new QueryWrapper<>();
                    wrapper.eq("create_time", reportSwap.getApplicationTime()).eq("create_user_id", userId);
                    List<Report> reportList = reportMapper.selectList(wrapper);
                    //获得申请人的值班信息
                    Report Appreport = reportList.get(0);
                    //申请人值班时间
                    LocalDateTime createTime1 = Appreport.getCreateTime();
                    QueryWrapper<Report> wrapper1 = new QueryWrapper<>();
                    wrapper1.eq("create_time", swapTime).eq("create_user_id", receiveUserId);
                    List<Report> reportList1 = reportMapper.selectList(wrapper1);
                    //获得调换人的值班信息
                    Report swapPort = reportList1.get(0);
                    //调换人值班时间
                    LocalDateTime createTime2 = swapPort.getCreateTime();
                    //交换时间后更新
                    Appreport.setCreateTime(createTime2);
                    swapPort.setCreateTime(createTime1);
                    reportMapper.updateById(swapPort);
                    reportMapper.updateById(Appreport);
                    //更新状态值
                    int i = reportSwapMapper.updateById(reportSwap);
                    //发送消息给申请人
                    iWxLoginService.sendTpMessage(sysUserService.findDetailById(reportSwap.getCreateUserId()).getUnionId(), "您和" + sysUserService.findDetailById(reportSwap.getReceiveUserId()).getRealName() + "备勤调换申请已通过", reportSwap.getId(), 0L);
                    iWxLoginService.sendTpMessage(sysUserService.findDetailById(reportSwap.getReceiveUserId()).getUnionId(), "您和" + sysUserService.findDetailById(reportSwap.getCreateUserId()).getRealName() + "备勤调换申请已通过", reportSwap.getId(), 0L);
                    iWxLoginService.sendTpMessage("15062289697", sysUserService.findDetailById(reportSwap.getCreateUserId()).getRealName() + "和" + sysUserService.findDetailById(reportSwap.getReceiveUserId()).getRealName() + "备勤日期已调换", reportSwap.getId(), 1L);
                     iWxLoginService.sendTpMessage("087003", sysUserService.findDetailById(reportSwap.getCreateUserId()).getRealName() + "和" + sysUserService.findDetailById(reportSwap.getCreateUserId()).getRealName() + "备勤日期已调换", reportSwap.getId(), 1L);
                } else {
                    reportSwap.setStatus(5);
                    int i = reportSwapMapper.updateById(reportSwap);
                    iWxLoginService.sendTpMessage(sysUserService.findDetailById(reportSwap.getCreateUserId()).getUnionId(),
                            "您和" + sysUserService.findDetailById(reportSwap.getReceiveUserId()).getRealName() + "备勤调换申请被拒绝,请重新选择提交", reportSwap.getId(), 0L);
                    iWxLoginService.sendTpMessage(sysUserService.findDetailById(reportSwap.getReceiveUserId()).getUnionId(),
                            "您和" + sysUserService.findDetailById(reportSwap.getCreateUserId()).getRealName() + "备勤调换申请被拒绝,请重新选择提交", reportSwap.getId(), 0L);
                }
                return 1;
            }
            //如果申请人的职位是辅警
            SysUser sysUser = sysUserService.findDetailById(reportSwap.getCreateUserId());
            if (sysUser.getPositionId() == 130) {
                QueryWrapper<UserDepartment> queryWrapper = new QueryWrapper<>();
                //查询申请人的部门id
                // TODO: 2021/6/16 0016  假设目前只有一个部门
                queryWrapper.eq("user_id", createUserId);
                List<UserDepartment> userDepartments = userDepartmentMapper.selectList(queryWrapper);
                //查询出所在部门id
                Long depId = userDepartments.get(0).getDepId();
                //判断所属部门 交警大队 刑警大队  指挥中心 是不是所属这三个部门中
                if (depId == 10002 || depId == 10019 || depId == 10014 || depId==1404352904213389314L) {
                    //如果申请人是这三个部门的 就去查询这个三个部门中的民警,并发送消息
                    QueryWrapper<UserDepartment> queryWrapper1 = new QueryWrapper<>();
                    queryWrapper1.eq("dep_id", depId);
                    //查询出该部门所有人员
                    List<UserDepartment> departments = userDepartmentMapper.selectList(queryWrapper1);
                    //遍历或者人员职位id
                    for (UserDepartment department : departments) {
                        SysUser user = sysUserService.findDetailById(department.getUserId());
                        //判断 职位民警就发送消息
                        if (user.getPositionId() == 128 && user.getDeId().equals(user.getDeId())) {
                            iWxLoginService.sendTpMessage(user.getUnionId(), "您有一条辅警备勤调换申请,请及时查看审核 ", reportSwap.getId(), 1L);
                            int i = reportSwapMapper.updateById(reportSwap);
                        }
                        //通知当事人
                    }
                    iWxLoginService.sendTpMessage(sysUserService.findDetailById(reportSwap.getCreateUserId()).getUnionId(), "您的备勤调换申请已同意,请等待部门民警审批通过", reportSwap.getId(), 0L);
                    iWxLoginService.sendTpMessage(sysUserService.findDetailById(reportSwap.getReceiveUserId()).getUnionId(), "您的备勤调换申请已同意,请等待部门民警审批通过", reportSwap.getId(), 0L);

                    return 1;
                }
            }
            //如果是民警
            if (sysUser.getPositionId() == 128) {
                //查找教导员和部门领导 发送消息
                QueryWrapper<UserDepartment> queryWrapper = new QueryWrapper<>();
                //查询申请人的部门id
                queryWrapper.eq("user_id", createUserId);
                List<UserDepartment> userDepartments = userDepartmentMapper.selectList(queryWrapper);
                //查询出所在部门id
                Long depId = userDepartments.get(0).getDepId();
                QueryWrapper<UserDepartment> queryWrapper1 = new QueryWrapper<>();
                queryWrapper1.eq("dep_id", depId);
                List<UserDepartment> departments = userDepartmentMapper.selectList(queryWrapper1);
                for (UserDepartment department : departments) {
                    //根据关联的人员查询出人员信息 判断等于职位是部门领导的
                    SysUser detailById = sysUserService.findDetailById(department.getUserId());
                    //如果是部门领导和教导员 同时发送消息
                    if (detailById.getPositionId() == 126 || detailById.getPositionId() == 137  ) {
                        //给部门领导发消息
                        iWxLoginService.sendTpMessage(sysUserService.findDetailById(department.getUserId()).getUnionId(), "您有一条部门成员备勤调换申请,请及时查看审核 ", reportSwap.getId(), 1L);
                    }
                }
                iWxLoginService.sendTpMessage(sysUserService.findDetailById(reportSwap.getCreateUserId()).getUnionId(), "您的备勤调换申请已同意,请等待部门领导审批通过", reportSwap.getId(), 0L);
                int i = reportSwapMapper.updateById(reportSwap);
                return 1;

            }
            QueryWrapper<UserDepartment> queryWrapper = new QueryWrapper<>();
            //查询申请人的部门id
            queryWrapper.eq("user_id", createUserId);
            List<UserDepartment> userDepartments = userDepartmentMapper.selectList(queryWrapper);
            //查询出所在部门id
            Long depId = userDepartments.get(0).getDepId();
            QueryWrapper<UserDepartment> queryWrapper1 = new QueryWrapper<>();
            queryWrapper1.eq("dep_id", depId);
            //申请人所在部门列表
            // TODO: 2021/6/14 现在是在部门表添加局领导id 改成在用户表是局领导的用户可以设置看管部门
            List<UserDepartment> departments = userDepartmentMapper.selectList(queryWrapper1);
            //如果申请人是部门领导 查询局长id发送消息给局长 审核
            if (sysUserService.findDetailById(createUserId).getPositionId() == 126) {
                Department department1 = departmentMapper.selectById(depId);
                Long ldeaderId = department1.getLeaderId();
                SysUser detailById = sysUserService.findDetailById(ldeaderId);
                iWxLoginService.sendTpMessage(detailById.getUnionId(), "您有一条成员备勤调换申请,请及时查看审核 ", reportSwap.getId(), 1L);
                iWxLoginService.sendTpMessage(sysUserService.findDetailById(reportSwap.getCreateUserId()).getUnionId(), "您的备勤调换申请已同意,请等待部门领导审批通过", reportSwap.getId(), 0L);
                int i = reportSwapMapper.updateById(reportSwap);
            } else {
                for (UserDepartment department : departments) {
                    //根据关联的人员查询出人员信息 判断等于职位是部门领导的
                    SysUser detailById = sysUserService.findDetailById(department.getUserId());
                    if (detailById.getPositionId() == 126) {
                        //给部门领导发消息
                        iWxLoginService.sendTpMessage(sysUserService.findDetailById(department.getUserId()).getUnionId(), "您有一条部门成员备勤调换申请,请及时查看审核 ", reportSwap.getId(), 1L);
                    }
                }
                iWxLoginService.sendTpMessage(sysUserService.findDetailById(reportSwap.getCreateUserId()).getUnionId(), "您的备勤调换申请已同意,请等待部门领导审批通过", reportSwap.getId(), 0L);
                int i = reportSwapMapper.updateById(reportSwap);
            }

        } else {
            int i = reportSwapMapper.updateById(reportSwap);
            iWxLoginService.sendTpMessage(sysUserService.findDetailById(reportSwap.getCreateUserId()).getUnionId(), "您的备勤调换申请已被拒绝,请重新选择", reportSwap.getId(), 0L);
        }
        return 1;
    }

    public List<Report> oneMonthWeek(String userId) {
        Date date = DateUtils.addDays(new Date(), 30);
        String pattern = "yyyy-MM-dd";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        String format = simpleDateFormat.format(date);
        String weekday= format+ " 00:00:00";
        String today= DateUtil.getDay()+ " 00:00:00";
        List<Report> reportSwaps = reportSwapMapper.oneMonthWeek(userId, today, weekday);
            return  reportSwaps;
    }
}
