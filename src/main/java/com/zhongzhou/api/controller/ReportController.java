package com.zhongzhou.api.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zhongzhou.api.entity.*;
import com.zhongzhou.api.mapper.PositionMapper;
import com.zhongzhou.api.mapper.SysUserMapper;
import com.zhongzhou.api.mapper.UserDepartmentMapper;
import com.zhongzhou.api.service.impl.DepartmentServiceImpl;
import com.zhongzhou.api.service.impl.ReportServiceImpl;
import com.zhongzhou.api.service.impl.UserDepartmentServiceImpl;
import com.zhongzhou.common.base.BaseController;
import com.zhongzhou.common.base.Pager;
import com.zhongzhou.common.bean.ReturnEntity;
import com.zhongzhou.common.bean.ReturnEntityError;
import com.zhongzhou.common.bean.ReturnEntitySuccess;
import com.zhongzhou.common.utils.Constants;
import com.zhongzhou.common.utils.FileDownloadUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 勤务报备 前端控制器
 * </p>
 *
 * @author wqc
 * @since 2021-06-05
 */
@RestController
@RequestMapping("/api/report")
@Slf4j
public class ReportController extends BaseController {
    @Resource
    private ReportServiceImpl reportService;
    @Resource
    private SysUserMapper sysUserMapper;
    @Resource
    private UserDepartmentMapper userDepartmentMapper;
    @Resource
    private UserDepartmentServiceImpl userDepartmentService;
    @Resource
    private DepartmentServiceImpl departmentService;
    @Resource
    private PositionMapper positionMapper;

    /**
     * 分页查询列表
     *
     * @param pager  分页信息
     * @param report Report
     * @return ReturnEntity
     */
    @GetMapping("/page")
    public ReturnEntity selectPageList(Pager<Report> pager, Report report,
                                       HttpServletRequest request, HttpServletResponse response) {
        try {
            QueryWrapper<Report> wrapper = new QueryWrapper<>();
            wrapper.like(StringUtils.isNotBlank(report.getUserName()), "username", report.getUserName());
            wrapper.eq(report.getCreateTime() != null, "create_time", report.getCreateTime());
            wrapper.eq(report.getDepId() != null , "dep_id" , report.getDepId());
            wrapper.orderByDesc("create_time");
            List<Report> records = reportService.page(pager, wrapper).getRecords();
            for (Report record : records) {
                //部门名称
                if (record.getDepId() != null) {
                    Department department = departmentService.getById(record.getDepId());
                    if (department!=null ){
                        record.setDepName(department.getDepName());
                    }
                }
                //职位
                Position position = positionMapper.selectById(record.getPositionId());
                if (position != null) {
                    record.setPoisitionName(position.getName());
                }
            }
            int count = reportService.count(wrapper);
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
     * @param report Report
     * @return ReturnEntity
     */
    @GetMapping("/list")
    public ReturnEntity selectList(Report report,
                                   HttpServletRequest request, HttpServletResponse response) {
        try {
            QueryWrapper<Report> wrapper = new QueryWrapper<>();
            wrapper.like(StringUtils.isNotBlank(report.getUserName()), "username", report.getUserName());
            wrapper.eq(report.getCreateTime() != null, "create_time", report.getCreateTime());
            wrapper.eq(report.getDepId() != null , "dep_id" , report.getDepId());
            wrapper.orderByDesc("create_time");
            List<Report> list = reportService.list(wrapper);
            for (Report record : list) {
                //部门名称
                if (record.getDepId() != null) {
                    Department department = departmentService.getById(record.getDepId());
                    if (department!=null ){
                        record.setDepName(department.getDepName());
                    }
                }
                //职位
                Position position = positionMapper.selectById(record.getPositionId());
                if (position != null) {
                    record.setPoisitionName(position.getName());
                }
            }
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
    public ReturnEntity selectById(@PathVariable("id") Long id) {
        try {
            Report report = reportService.getById(id);
            if (null != report) {
                //部门名称
                if (report.getDepId() != null) {
                    Department department = departmentService.getById(report.getDepId());
                    if (department!=null ){
                        report.setDepName(department.getDepName());
                    }
                }
                //职位
                Position position = positionMapper.selectById(report.getPositionId());
                if (position != null) {
                    report.setPoisitionName(position.getName());
                }
                return new ReturnEntitySuccess(Constants.MSG_FIND_SUCCESS, report);
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
     * @param report Report
     * @param result BindingResult
     * @return ReturnEntity
     */
    @PostMapping("/add")
    public ReturnEntity save(@Validated @RequestBody Report report, BindingResult result,
                             HttpServletRequest request, HttpServletResponse response) {
        if (result.hasErrors()) {
            FieldError fieldError = result.getFieldErrors().get(0);
            String errorMsg = fieldError.getDefaultMessage();
            if (Constants.MSG_ERROR_CANNOT_NULL.equals(errorMsg)) {
                errorMsg = fieldError.getField() + fieldError.getDefaultMessage();
            }
            return new ReturnEntityError(errorMsg, null, report);
        } else {
            try {
                report.setUserName(sysUserMapper.selectById(report.getCreateUserId()).getRealName());
                if (reportService.save(report)) {
                    return new ReturnEntitySuccess(Constants.MSG_INSERT_SUCCESS, report);
                } else {
                    return new ReturnEntityError(Constants.MSG_INSERT_FAILED, report);
                }

            } catch (Exception e) {
                e.printStackTrace();
                log.error("[" + Constants.MSG_INSERT_FAILED + "]:{}", e.getMessage());
                return new ReturnEntityError(Constants.MSG_INSERT_FAILED, report);
            }
        }
    }

    /**
     * 修改
     *
     * @param id     主键
     * @param report Report
     * @param result BindingResult
     * @return ReturnEntity
     */
    @PutMapping("/edit/{id}")
    public ReturnEntity updateById(@PathVariable("id") Long id, @Validated @RequestBody Report report, BindingResult result,
                                   HttpServletRequest request, HttpServletResponse response) {
        if (result.hasErrors()) {
            return new ReturnEntityError(result.getFieldErrors().get(0).getDefaultMessage(), report);
        } else {
            try {
                if (null == reportService.getById(id)) {
                    return new ReturnEntityError(Constants.MSG_FIND_NOT_FOUND, report);
                } else {
                    if (reportService.updateById(report)) {
                        return new ReturnEntitySuccess(Constants.MSG_UPDATE_SUCCESS, report);
                    } else {
                        return new ReturnEntityError(Constants.MSG_UPDATE_FAILED, report);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                log.error("[id:{} " + Constants.MSG_UPDATE_FAILED + "]:{}", id, e.getMessage());
                return new ReturnEntityError(Constants.MSG_UPDATE_FAILED, report);
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
            if (null == reportService.getById(id)) {
                return new ReturnEntityError(Constants.MSG_FIND_NOT_FOUND, id);
            } else {
                if (reportService.removeById(id)) {
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
     * 下载勤务报备导入模板
     *
     * @param request  HttpServletRequest
     * @param response HttpServletResponse
     */
    @GetMapping("/downReportTemplate")
    public void downReportTemplate(HttpServletRequest request, HttpServletResponse response) {
        try {
            String filePath = "/home/template/ServiceReportWeb";
            String fileName = "警力部署表.xlsx";
            FileDownloadUtil.download(filePath, fileName, request, response);
            log.info("警力部署表下载成功");
        } catch (Exception e) {
            e.printStackTrace();
            log.error("警力部署表下载成功");
            throw new RuntimeException("警力部署表下载成功");
        }
    }

    /**
     * 导入
     *
     * @param file 导入文件
     * @return ReturnEntity
     */
    @PostMapping("/importExcel")
    public ReturnEntity importExcel(@RequestParam("file") MultipartFile file) {
        try {
            //
            reportService.importExcel(file);
            //
            log.info(Constants.MSG_UPLOAD_SUCCESS);
            return new ReturnEntitySuccess(Constants.MSG_UPLOAD_SUCCESS);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("[" + Constants.MSG_UPLOAD_FAILED + "]:{}", e.getMessage());
            return new ReturnEntityError(Constants.MSG_UPLOAD_FAILED);
        }
    }

    /**
     * 根据部门id查询当前部门下所有人员
     */
    @GetMapping("/selectPeoByDep/{id}")
    public ReturnEntity selectPeoByDep(@PathVariable("id") Long id, HttpServletRequest request) {
        QueryWrapper<UserDepartment> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("dep_id", id);
        List<SysUser> sysUsers = new ArrayList<>();
        List<UserDepartment> userDepartments = userDepartmentMapper.selectList(queryWrapper);
        Long userId1 = tokenController.getUserId(request);
        for (UserDepartment userDepartment : userDepartments) {
            if (!userDepartment.getUserId().equals(userId1)) {
                Long userId = userDepartment.getUserId();
                SysUser sysUser = sysUserMapper.selectById(userId);
                sysUsers.add(sysUser);
            }
            QueryWrapper<UserDepartment> queryWrapper1 = new QueryWrapper<>();
            queryWrapper1.eq("user_id", userId1);
            userDepartment.delete(queryWrapper1);

        }
        return new ReturnEntitySuccess(Constants.MSG_FIND_SUCCESS, sysUsers);


    }


}
