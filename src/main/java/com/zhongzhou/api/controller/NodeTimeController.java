package com.zhongzhou.api.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zhongzhou.api.entity.NodeTime;
import com.zhongzhou.api.service.impl.NodeTimeServiceImpl;
import com.zhongzhou.common.base.BaseController;
import com.zhongzhou.common.base.Pager;
import com.zhongzhou.common.bean.ReturnEntity;
import com.zhongzhou.common.bean.ReturnEntityError;
import com.zhongzhou.common.bean.ReturnEntitySuccess;
import com.zhongzhou.common.utils.Constants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author wqc
 * @since 2021-06-12
 */
@RestController
@RequestMapping("/api/nodeTime")
@Slf4j
public class NodeTimeController extends BaseController {
    @Resource
    private NodeTimeServiceImpl nodeTimeService;

    /**
     * 分页查询列表
     *
     * @param pager    分页信息
     * @param nodeTime NodeTime
     * @return ReturnEntity
     */
    @GetMapping("/page")
    public ReturnEntity selectPageList(Pager<NodeTime> pager, NodeTime nodeTime,
                                       HttpServletRequest request, HttpServletResponse response) {
        try {
//
            QueryWrapper<NodeTime> wrapper = new QueryWrapper<>();
            wrapper.like(StringUtils.isNotBlank(nodeTime.getName()), "name", nodeTime.getName());
            if (!StringUtils.isEmpty(nodeTime.getMonth())) {
                if (Integer.parseInt(nodeTime.getMonth()) < 10) {
                    nodeTime.setMonth("0" + nodeTime.getMonth());
                }
                wrapper.eq("month", nodeTime.getMonth());
            }
            if (!StringUtils.isEmpty(nodeTime.getDay())) {
                if (Integer.parseInt(nodeTime.getDay()) < 10) {
                    nodeTime.setDay("0" + nodeTime.getDay());
                }
                wrapper.eq("day", nodeTime.getDay());
            }
            wrapper.eq(StringUtils.isNotBlank(nodeTime.getType()), "type", nodeTime.getType());
            List<NodeTime> records = nodeTimeService.page(pager, wrapper).getRecords();
            int count = nodeTimeService.count(wrapper);
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
     * @param nodeTime NodeTime
     * @return ReturnEntity
     */
    @GetMapping("/list")
    public ReturnEntity selectList(NodeTime nodeTime,
                                   HttpServletRequest request, HttpServletResponse response) {
        try {
            QueryWrapper<NodeTime> wrapper = new QueryWrapper<>();
            List<NodeTime> list = nodeTimeService.list(wrapper);
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
            NodeTime nodeTime = nodeTimeService.getById(id);
            if (null != nodeTime) {
                return new ReturnEntitySuccess(Constants.MSG_FIND_SUCCESS, nodeTime);
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
     * @param nodeTime NodeTime
     * @param result   BindingResult
     * @return ReturnEntity
     */
    @PostMapping("/add")
    public ReturnEntity save(@Validated @RequestBody NodeTime nodeTime, BindingResult result,
                             HttpServletRequest request, HttpServletResponse response) {
        if (result.hasErrors()) {
            FieldError fieldError = result.getFieldErrors().get(0);
            String errorMsg = fieldError.getDefaultMessage();
            if (Constants.MSG_ERROR_CANNOT_NULL.equals(errorMsg)) {
                errorMsg = fieldError.getField() + fieldError.getDefaultMessage();
            }
            return new ReturnEntityError(errorMsg, null, nodeTime);
        } else {
            try {
                nodeTime.setCreateUserId(tokenController.getUserId(request));
                if (nodeTimeService.saveNodeTime(nodeTime)) {
                    return new ReturnEntitySuccess(Constants.MSG_INSERT_SUCCESS, nodeTime);
                } else {
                    return new ReturnEntityError("提供的时间节点已存在", nodeTime);
                }
            } catch (Exception e) {
                e.printStackTrace();
                log.error("[" + Constants.MSG_INSERT_FAILED + "]:{}", e.getMessage());
                return new ReturnEntityError(Constants.MSG_INSERT_FAILED, nodeTime);
            }
        }
    }

    /**
     * 修改
     *
     * @param id       主键
     * @param nodeTime NodeTime
     * @param result   BindingResult
     * @return ReturnEntity
     */
    @PutMapping("/edit/{id}")
    public ReturnEntity updateById(@PathVariable("id") Long id, @Validated @RequestBody NodeTime nodeTime, BindingResult result,
                                   HttpServletRequest request, HttpServletResponse response) {
        if (result.hasErrors()) {
            return new ReturnEntityError(result.getFieldErrors().get(0).getDefaultMessage(), nodeTime);
        } else {
            try {
                if (null == nodeTimeService.getById(id)) {
                    return new ReturnEntityError(Constants.MSG_FIND_NOT_FOUND, nodeTime);
                } else {
                    nodeTime.setId(id);
                    if (nodeTimeService.updateNodeTimeById(nodeTime)) {
                        return new ReturnEntitySuccess(Constants.MSG_UPDATE_SUCCESS, nodeTime);
                    } else {
                        return new ReturnEntityError("提供的时间节点已存在", nodeTime);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                log.error("[id:{} " + Constants.MSG_UPDATE_FAILED + "]:{}", id, e.getMessage());
                return new ReturnEntityError(Constants.MSG_UPDATE_FAILED, nodeTime);
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
            if (null == nodeTimeService.getById(id)) {
                return new ReturnEntityError(Constants.MSG_FIND_NOT_FOUND, id);
            } else {
                if (nodeTimeService.removeById(id)) {
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

}
