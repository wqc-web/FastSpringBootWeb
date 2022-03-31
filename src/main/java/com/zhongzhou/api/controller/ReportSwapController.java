package com.zhongzhou.api.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zhongzhou.api.entity.Report;
import com.zhongzhou.api.entity.ReportSwap;
import com.zhongzhou.api.entity.SysUser;
import com.zhongzhou.api.entity.UserDepartment;
import com.zhongzhou.api.mapper.DepartmentMapper;
import com.zhongzhou.api.mapper.ReportMapper;
import com.zhongzhou.api.mapper.SysUserMapper;
import com.zhongzhou.api.mapper.UserDepartmentMapper;
import com.zhongzhou.api.service.IWxLoginService;
import com.zhongzhou.api.service.impl.ReportSwapServiceImpl;
import com.zhongzhou.api.service.impl.SysUserServiceImpl;
import com.zhongzhou.common.base.BaseController;
import com.zhongzhou.common.base.Pager;
import com.zhongzhou.common.bean.ReturnEntity;
import com.zhongzhou.common.bean.ReturnEntityError;
import com.zhongzhou.common.bean.ReturnEntitySuccess;
import com.zhongzhou.common.utils.Constants;
import com.zhongzhou.common.utils.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.hibernate.validator.constraints.Length;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * <p>
 * 勤务报备对调
 * </p>
 *
 * @author wqc
 * @since 2021-06-05
 */
@RestController
@RequestMapping("/api/reportSwap")
@Slf4j
public class ReportSwapController extends BaseController {
    @Resource
    private ReportSwapServiceImpl reportSwapService;
    @Resource
    private SysUserServiceImpl sysUserService;
    @Resource
    private IWxLoginService iWxLoginService;
    @Resource
    private SysUserMapper sysUserMapper;
    @Resource
    private ReportMapper reportMapper;
    @Resource
    private UserDepartmentMapper userDepartmentMapper;
    @Resource
    private DepartmentMapper departmentMapper;


    /**
     * 分页查询列表
     *
     * @param pager      分页信息
     * @param reportSwap ReportSwap
     * @return ReturnEntity
     */
    @GetMapping("/page")
    public ReturnEntity selectPageList(Pager<ReportSwap> pager, ReportSwap reportSwap,
                                       HttpServletRequest request, HttpServletResponse response) {
        try {
            List<ReportSwap> records = reportSwapService.queryPage(pager, reportSwap);
            for (ReportSwap record : records) {
                LocalDateTime applicationTime = record.getCreateTime();
                LocalDateTime now = LocalDateTime.now();
                java.time.Duration duration = java.time.Duration.between(applicationTime, now);
                long minutes = duration.toMinutes();
                System.out.println(minutes);
                if (minutes > 3) {
                    record.setStatus(3);
                }
            }
            Integer count = reportSwapService.queryCount(reportSwap);
            return new ReturnEntitySuccess(Constants.MSG_FIND_SUCCESS, count, records);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("[" + Constants.MSG_FIND_FAILED + "]:" + e.getMessage());
            return new ReturnEntityError(Constants.MSG_FIND_FAILED, null, null);
        }
    }

    @GetMapping("/pageList")

    public ReturnEntity pageList(Pager<ReportSwap> pager, ReportSwap reportSwap,
                                 HttpServletRequest request, HttpServletResponse response) {
        ArrayList<ReportSwap> reportSwaps = new ArrayList<>();

        try {
            if (StringUtils.isNotBlank(reportSwap.getCreateRealName())){
                QueryWrapper<ReportSwap> wrapper = new QueryWrapper<>();
                wrapper.eq(reportSwap.getStatus()!=null,"status",reportSwap.getStatus());
                List<ReportSwap> records = reportSwapService.page(pager, wrapper).getRecords();
                for (ReportSwap record : records) {
                    record.setCreateRealName(sysUserService.findDetailById(record.getCreateUserId()).getRealName());
                    record.setReceiveRealName(sysUserService.findDetailById(record.getReceiveUserId()).getRealName());
                    if (record.getCreateRealName().contains(reportSwap.getCreateRealName())){
                        reportSwaps.add(record);
                    }
                }
                return new ReturnEntitySuccess(Constants.MSG_FIND_SUCCESS, reportSwaps.size(), reportSwaps);
            }
            QueryWrapper<ReportSwap> wrapper = new QueryWrapper<>();
            wrapper.eq(reportSwap.getStatus()!=null,"status",reportSwap.getStatus());
            List<ReportSwap> records = reportSwapService.page(pager, wrapper).getRecords();
            for (ReportSwap record : records) {
                record.setCreateRealName(sysUserService.findDetailById(record.getCreateUserId()).getRealName());
                record.setReceiveRealName(sysUserService.findDetailById(record.getReceiveUserId()).getRealName());
                LocalDateTime applicationTime = record.getCreateTime();
                LocalDateTime now = LocalDateTime.now();
                java.time.Duration duration = java.time.Duration.between(applicationTime, now);
                long minutes = duration.toMinutes();
                System.out.println(minutes);
                if (record.getStatus()==0){
                    if (sysUserService.findDetailById(record.getCreateUserId()).getPositionId()==126 || sysUserService.findDetailById(reportSwap.getCreateUserId()).getPositionId()==125){
                        if (minutes > 5) {
                            record.setStatus(3);
                        }
                    }
                    if (minutes > 3) {
                        record.setStatus(3);
                    }
                }

            }
            int count = reportSwapService.count(wrapper);
            return new ReturnEntitySuccess(Constants.MSG_FIND_SUCCESS, count, records);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("[" + Constants.MSG_FIND_FAILED + "]:" + e.getMessage());
            return new ReturnEntityError(Constants.MSG_FIND_FAILED, null, null);
        }
    }


    /**
     * 查询所有列表
     *
     * @param reportSwap ReportSwap
     * @return ReturnEntity
     */
    @GetMapping("/list")
    public ReturnEntity selectList(ReportSwap reportSwap,
                                   HttpServletRequest request, HttpServletResponse response) {
        try {
            List<ReportSwap> list = reportSwapService.queryPage(null, reportSwap);
            return new ReturnEntitySuccess(Constants.MSG_FIND_SUCCESS, list.size(), list);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("[" + Constants.MSG_FIND_FAILED + "]:" + e.getMessage());
            return new ReturnEntityError(Constants.MSG_FIND_FAILED, null, null);
        }
    }

    /**
     * 查询详情
     *
     * @param id 主键
     * @return ReturnEntity
     */
    @GetMapping("/detail/{id}")
    public ReturnEntity selectById(@PathVariable("id") Long id,HttpServletRequest request,HttpServletResponse response) {
        try {
            ReportSwap reportSwap = reportSwapService.getById(id);
            if (null != reportSwap) {
                    Long userId1 = tokenController.getUserId(request);
                SysUser sysUser = sysUserService.findDetailById(userId1);
                if (userId1.equals(reportSwap.getCreateUserId())) {
                        reportSwap.setMode(1);
                    }else {
                        reportSwap.setMode(2);
                    }
                if (sysUser.getPositionId()==126){
                    reportSwap.setDeLeaderMode(1);
                }else {
                    reportSwap.setDeLeaderMode(2);

                }
                if (sysUser.getPositionId()==125){
                    reportSwap.setLeaderMode(1);
                }else {
                    reportSwap.setLeaderMode(2);

                }
                    reportSwap.setCreateRealName(sysUserMapper.selectById(reportSwap.getCreateUserId()).getRealName());
                    reportSwap.setReceiveRealName(sysUserMapper.selectById(reportSwap.getReceiveUserId()).getRealName());
                    LocalDateTime applicationTime = reportSwap.getCreateTime();
                    LocalDateTime now = LocalDateTime.now();
                    java.time.Duration duration = java.time.Duration.between(applicationTime, now);
                    long minutes = duration.toMinutes();
                    System.out.println(minutes);
                    if (sysUserService.findDetailById(reportSwap.getCreateUserId()).getPositionId()==126 || sysUserService.findDetailById(reportSwap.getCreateUserId()).getPositionId()==125){
                        if (minutes > 5) {
                            reportSwap.setStatus(3);
                            reportSwapService.updateById(reportSwap);
                        }
                    }
                    if (minutes > 3) {
                        reportSwap.setStatus(3);
                        reportSwapService.updateById(reportSwap);
                    }
                return new ReturnEntitySuccess(Constants.MSG_FIND_SUCCESS, reportSwap);
            } else {
                return new ReturnEntitySuccess(Constants.MSG_FIND_NOT_FOUND);
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("[id:{} " + Constants.MSG_FIND_FAILED + "]:{}", id, e.getMessage());
            return new ReturnEntityError(Constants.MSG_FIND_FAILED);
        }
    }

    /**
     * 新增
     *
     * @param reportSwap ReportSwap
     * @param result     BindingResult
     * @return ReturnEntity
     */
    @PostMapping("/add")
    public ReturnEntity save(@Validated @RequestBody ReportSwap reportSwap, BindingResult result,
                             HttpServletRequest request, HttpServletResponse response) {
            if (result.hasErrors()) {
                FieldError fieldError = result.getFieldErrors().get(0);
                String errorMsg = fieldError.getDefaultMessage();
                if (Constants.MSG_ERROR_CANNOT_NULL.equals(errorMsg)) {
                    errorMsg = fieldError.getField() + fieldError.getDefaultMessage();
                }
                return new ReturnEntityError(errorMsg, null, reportSwap);
            } else {
                try {
                    Long userId = tokenController.getUserId(request);
                    reportSwap.setCreateUserId(userId);
                    reportSwap.setCreateTime(LocalDateTime.now());
                    reportSwap.setReceiveRealName(sysUserService.findDetailById(reportSwap.getReceiveUserId()).getRealName());
                    if (reportSwapService.save(reportSwap)) {
                        if (null != reportSwap) {
                            Long userId2 =  tokenController.getUserId(request);
                            SysUser sysUser = sysUserService.findDetailById(userId2);
                            if (userId2.equals(reportSwap.getCreateUserId())) {
                                reportSwap.setMode(1);
                            } else {
                                reportSwap.setMode(2);
                            }
                            if (sysUser.getPositionId() == 126) {
                                reportSwap.setDeLeaderMode(1);
                            } else {
                                reportSwap.setDeLeaderMode(2);

                            }
                            if (sysUser.getPositionId() == 125) {
                                reportSwap.setLeaderMode(1);
                            } else {
                                reportSwap.setLeaderMode(2);

                            }
                        }

                        iWxLoginService.sendTpMessage(sysUserService.findDetailById(reportSwap.getReceiveUserId()).getUnionId(), "您有新的备勤调换申请,请及时查看!测试数据", reportSwap.getId(), 0L);
                        return new ReturnEntitySuccess(Constants.MSG_INSERT_SUCCESS, reportSwap);
                    } else {
                        return new ReturnEntityError(Constants.MSG_INSERT_FAILED, reportSwap);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    log.error("[" + Constants.MSG_INSERT_FAILED + "]:{}", e.getMessage());
                    return new ReturnEntityError(Constants.MSG_INSERT_FAILED, reportSwap);
                }
            }


    }

    /**
     * 删除
     *
     * @param id 主键
     * @return ReturnEntity
     */
    @DeleteMapping("/delete/{id}")
    public ReturnEntity deleteById(@PathVariable("id") Long id) {
        try {
            if (null == reportSwapService.getById(id)) {
                return new ReturnEntityError(Constants.MSG_FIND_NOT_FOUND, id);
            } else {
                if (reportSwapService.removeById(id)) {
                    return new ReturnEntitySuccess(Constants.MSG_DELETE_SUCCESS, id);
                } else {
                    return new ReturnEntityError(Constants.MSG_DELETE_FAILED, id);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("[id:{} " + Constants.MSG_DELETE_FAILED + "]:{}", id, e.getMessage());
            return new ReturnEntityError(Constants.MSG_DELETE_FAILED, id);
        }
    }

    /**
     * 根据申请人id去查询当前部门所有人员
     */
    @GetMapping("/selectDepPepByPreId")
    public ReturnEntity selectDepPepByPreId(String selfTime, String swapTime, HttpServletRequest request, HttpServletResponse response) {
        Long id =tokenController.getUserId(request);
        System.out.println(id);
        System.out.println(selfTime);
        System.out.println(swapTime);
        List<SysUser> sysUsers = reportSwapService.selectDepPepByPreId(id, selfTime, swapTime);
        return new ReturnEntitySuccess(Constants.MSG_FIND_SUCCESS, sysUsers);

    }

    /**
     * 领导同意或拒绝
     */
    @PutMapping("/operationReport/{id}")
    public ReturnEntity operationReport(@PathVariable("id") Long id, @RequestBody ReportSwap reportSwap) {
        reportSwapService.operationReport(id, reportSwap.getOperation());
        return new ReturnEntitySuccess(Constants.MSG_UPDATE_SUCCESS);
    }


    /**
     * 同事同意和拒绝
     */
    @PutMapping("/checkReport/{id}")
    public ReturnEntity checkReport(@PathVariable("id") Long id, @RequestBody ReportSwap reportSwap) {
        reportSwapService.checkReport(id, reportSwap.getOperation());
        return new ReturnEntitySuccess(Constants.MSG_UPDATE_SUCCESS);

    }

    /**
     * 消息推送跳转
     */

    @GetMapping("/messagePush")
    public ReturnEntity messagePush(HttpServletRequest request, HttpServletResponse response) {
        //获取跳转过去用户的userid
        Long userId = tokenController.getUserId(request);
        ReportSwap reportSwap = reportSwapService.messagePush(userId);
        return new ReturnEntitySuccess(Constants.MSG_FIND_SUCCESS, reportSwap);
    }

    /**
     * 查询申请人的部门人员
     */
    @GetMapping("/departList")
    public ReturnEntity departList(HttpServletRequest request,String time){
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime createTime = LocalDateTime.parse(time + " 00:00:00", df);
       Long userId =tokenController.getUserId(request);
        System.out.println(userId);
        SysUser sysUser = sysUserService.findDetailById(userId);
        //查找教导员和部门领导 发送消息
        QueryWrapper<UserDepartment> queryWrapper = new QueryWrapper<>();
        //查询申请人的部门id
        queryWrapper.eq("user_id", userId);
        List<UserDepartment> userDepartments = userDepartmentMapper.selectList(queryWrapper);
        //查询出所在部门id
        Long depId = userDepartments.get(0).getDepId();
        QueryWrapper<UserDepartment> queryWrapper1 = new QueryWrapper<>();
        queryWrapper1.eq("dep_id", depId);
        List<UserDepartment> departments = userDepartmentMapper.selectList(queryWrapper1);
        ArrayList<SysUser> sysUsers = new ArrayList<>();
        //如果是民警和班子成员
        if (sysUser.getPositionId()==128 ||sysUser.getPositionId()==127) {
            for (UserDepartment department : departments) {
                SysUser detailById = sysUserService.findDetailById(department.getUserId());
                QueryWrapper<Report> wapper = new QueryWrapper<>();
                wapper.eq("create_time", createTime);
                wapper.eq("username", detailById.getRealName());
                List<Report> reportSwaps = reportSwapService.oneMonthWeek(detailById.getRealName());
                List<Report> reports = reportMapper.selectList(wapper);
                if (!detailById.getId().equals(userId) && reports.size() == 0) {
                    detailById.setSalt("1");
                } else {
                    detailById.setSalt("2");
                }
                if (detailById.getPositionId() == 128 ||detailById.getPositionId() == 127 || detailById.getPositionId() == 126) {
                    if (!detailById.getId().equals(userId)){
                        sysUsers.add(detailById);

                    }

                }
            }
            //如果是部门领导
        }else if (sysUser.getPositionId()==126) {
            for (UserDepartment department : departments) {
                SysUser detailById = sysUserService.findDetailById(department.getUserId());
                QueryWrapper<Report> wapper = new QueryWrapper<>();
                wapper.eq("create_time", createTime);
                wapper.eq("username", detailById.getRealName());
                List<Report> reportSwaps = reportSwapService.oneMonthWeek(detailById.getRealName());
                List<Report> reports = reportMapper.selectList(wapper);
                if (!detailById.getId().equals(userId) && reports.size() == 0) {
                    detailById.setSalt("1");
                } else {
                    detailById.setSalt("2");
                }
                if (detailById.getPositionId() == 127 ||detailById.getPositionId() == 128) {
                    if (!detailById.getId().equals(userId)){
                        sysUsers.add(detailById);

                    }

                }
            }
            //如果是辅警
        }else if (sysUser.getPositionId()==130){
            for (UserDepartment department : departments) {
                SysUser detailById = sysUserService.findDetailById(department.getUserId());
                QueryWrapper<Report> wapper = new QueryWrapper<>();
                wapper.eq("create_time",createTime);
                wapper.eq("username",detailById.getRealName());
                List<Report> reportSwaps = reportSwapService.oneMonthWeek(detailById.getRealName());
                List<Report> reports = reportMapper.selectList(wapper);
                if (!detailById.getId().equals(userId) && reports.size()==0){
                    detailById.setSalt("1");
                }else {
                    detailById.setSalt("2");
                }
                Integer deId = detailById.getDeId();
                Integer deId1 = sysUser.getDeId();
                if (detailById.getDeId()!=null){
                    if ((detailById.getPositionId() == 130 || detailById.getPositionId() == 132 ||detailById.getPositionId() == 133) && deId.equals(deId1)) {
                        if (!detailById.getId().equals(userId)){
                            sysUsers.add(detailById);
                        }
                    }
                }

            }

        }else   {
            for (UserDepartment department : departments) {
                SysUser detailById = sysUserService.findDetailById(department.getUserId());
                QueryWrapper<Report> wapper = new QueryWrapper<>();
                wapper.eq("create_time", createTime);
                wapper.eq("username", detailById.getRealName());
                List<Report> reportSwaps = reportSwapService.oneMonthWeek(detailById.getRealName());
                List<Report> reports = reportMapper.selectList(wapper);
                if (!detailById.getId().equals(userId) && reports.size() == 0) {
                    detailById.setSalt("1");
                } else {
                    detailById.setSalt("2");
                }
                if (!detailById.getId().equals(userId)) {
                    sysUsers.add(detailById);
                }
            }
        }

        //如果申请者是部门领导

        return new ReturnEntitySuccess(Constants.MSG_FIND_SUCCESS, sysUsers);

    }
    /**
     * 查询当前人最近一个月值班的日期
     */
    @GetMapping("/oneMonthWeek")
    public ReturnEntity  oneMonthWeek (HttpServletRequest request,String userName,HttpServletResponse response){
        Long userId = tokenController.getUserId(request,response);
        String name = sysUserService.findDetailById(userId).getUserName();
        if (userName!=null) {
            List<Report> reportSwaps = reportSwapService.oneMonthWeek(userName);
            for (int i = 0; i < reportSwaps.size(); i++) {
                LocalDateTime createTime = reportSwaps.get(i).getCreateTime();
                QueryWrapper<Report> wapper = new QueryWrapper<>();
                wapper.eq("create_time",createTime);
                wapper.eq("username",name);
                List<Report> reports = reportMapper.selectList(wapper);
                if (reports.size()!=0){
                    reportSwaps.remove(i);
                }
            }
          return new ReturnEntitySuccess(Constants.MSG_FIND_SUCCESS, reportSwaps);
      }else {
                String userName1 = sysUserService.findDetailById(userId).getUserName();
            List<Report> reportSwaps = reportSwapService.oneMonthWeek(userName1);
            return new ReturnEntitySuccess(Constants.MSG_FIND_SUCCESS, reportSwaps);
        }

    }
    /**
     * 判断当前人当天是否备勤
     */
    @GetMapping("/reportExist")
    public ReturnEntity  reportExist(HttpServletRequest request,String time){
       Long userId1 = tokenController.getUserId(request);
        QueryWrapper<Report> reportQueryWrapper = new QueryWrapper<>();
        reportQueryWrapper.eq("create_user_id", userId1);
        reportQueryWrapper.eq("create_time", time+" 00:00:00");
        List<Report> reports = reportMapper.selectList(reportQueryWrapper);
        if (reports.size()!=0 ){
            return new ReturnEntitySuccess(Constants.MSG_FIND_SUCCESS, 1);
        }else {
            return new ReturnEntitySuccess(Constants.MSG_FIND_SUCCESS, 2);

        }

    }

}
