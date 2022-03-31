package com.zhongzhou.api.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zhongzhou.api.entity.Node;
import com.zhongzhou.api.service.impl.NodeServiceImpl;
import com.zhongzhou.common.base.BaseController;
import com.zhongzhou.common.base.Pager;
import com.zhongzhou.common.bean.ReturnEntity;
import com.zhongzhou.common.bean.ReturnEntityError;
import com.zhongzhou.common.bean.ReturnEntitySuccess;
import com.zhongzhou.common.utils.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 敏感节点 前端控制器
 * </p>
 *
 * @author wqc
 * @since 2021-06-05
 */
@RestController
@RequestMapping("/api/node")
@Slf4j
public class NodeController extends BaseController {
    @Resource
    private NodeServiceImpl nodeService;

    /**
     * 分页查询列表
     *
     * @param pager 分页信息
     * @param node  Node
     * @return ReturnEntity
     */
    @GetMapping("/page")
    public ReturnEntity selectPageList(Pager<Node> pager, Node node,
                                       HttpServletRequest request, HttpServletResponse response) {
        try {
            QueryWrapper<Node> wrapper = new QueryWrapper<>();
            wrapper.eq(node.getReportStatus() != null, "report_status", node.getReportStatus());
            List<Node> records = nodeService.page(pager, wrapper).getRecords();
            int count = nodeService.count(wrapper);
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
     * @param node Node
     * @return ReturnEntity
     */
    @GetMapping("/list")
    public ReturnEntity selectList(Node node,
                                   HttpServletRequest request, HttpServletResponse response) {
        try {
            QueryWrapper<Node> wrapper = new QueryWrapper<>();
            List<Node> list = nodeService.list(wrapper);
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
            Node node = nodeService.getById(id);
            if (null != node) {
                return new ReturnEntitySuccess(Constants.MSG_FIND_SUCCESS, node);
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
     * @param node   Node
     * @param result BindingResult
     * @return ReturnEntity
     */
    @PostMapping("/add")
    public ReturnEntity save(@Validated @RequestBody Node node, BindingResult result,
                             HttpServletRequest request, HttpServletResponse response) {
        if (result.hasErrors()) {
            FieldError fieldError = result.getFieldErrors().get(0);
            String errorMsg = fieldError.getDefaultMessage();
            if (Constants.MSG_ERROR_CANNOT_NULL.equals(errorMsg)) {
                errorMsg = fieldError.getField() + fieldError.getDefaultMessage();
            }
            return new ReturnEntityError(errorMsg, null, node);
        } else {
            try {
//                Long userId = tokenController.getUserId(request);
//                if (userId != null) {
//                    node.setCreateUserId(userId);
//                }
                if (nodeService.saveNode(node)) {
                    return new ReturnEntitySuccess(Constants.MSG_INSERT_SUCCESS, node);
                } else {
                    return new ReturnEntityError("提供的时间段存在重叠", node);
                }
            } catch (Exception e) {
                e.printStackTrace();
                log.error("[" + Constants.MSG_INSERT_FAILED + "]:{}", e.getMessage());
                return new ReturnEntityError(Constants.MSG_INSERT_FAILED, node);
            }
        }
    }

    /**
     * 修改
     *
     * @param id     主键
     * @param node   Node
     * @param result BindingResult
     * @return ReturnEntity
     */
    @PutMapping("/edit/{id}")
    public ReturnEntity updateById(@PathVariable("id") Long id, @Validated @RequestBody Node node, BindingResult result,
                                   HttpServletRequest request, HttpServletResponse response) {
        if (result.hasErrors()) {
            return new ReturnEntityError(result.getFieldErrors().get(0).getDefaultMessage(), node);
        } else {
            try {
                if (null == nodeService.getById(id)) {
                    return new ReturnEntityError(Constants.MSG_FIND_NOT_FOUND, node);
                } else {
                    node.setId(id);
//                    Long userId = tokenController.getUserId(request);
//                    if (userId != null) {
//                        node.setLastUpdateUserId(userId);
//                    }
                    if (nodeService.updateNodeById(node)) {
                        return new ReturnEntitySuccess(Constants.MSG_UPDATE_SUCCESS, node);
                    } else {
                        return new ReturnEntityError("提供的时间段存在重叠", node);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                log.error("[id:{} " + Constants.MSG_UPDATE_FAILED + "]:{}", id, e.getMessage());
                return new ReturnEntityError(Constants.MSG_UPDATE_FAILED, node);
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
            if (null == nodeService.getById(id)) {
                return new ReturnEntityError(Constants.MSG_FIND_NOT_FOUND, id);
            } else {
                if (nodeService.removeById(id)) {
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
     * 根据时间返回规则
     *
     * @param time 时间段
     * @return 结果
     */
    @GetMapping("/ruleByTime")
    public ReturnEntity queryRuleByTime(@RequestParam("time") String time) {
        Map<String, Integer> ruleMap = nodeService.queryRuleByTime(time);
        return new ReturnEntitySuccess(Constants.MSG_FIND_SUCCESS, ruleMap);
    }

    /**
     * 勤务规则上下架
     *
     * @param id      id
     * @param node    状态
     * @param request 请求
     * @return 结果
     */
    @PutMapping("/updateStatus/{id}")
    public ReturnEntity updateStatus(@PathVariable("id") Long id, @RequestBody Node node, HttpServletRequest request) {
        if (nodeService.getById(id) == null) {
            return new ReturnEntityError(Constants.MSG_FIND_NOT_FOUND, id);
        }
        node.setId(id);
        node.setLastUpdateUserId(tokenController.getUserId(request));
        node.setLastUpdateTime(LocalDateTime.now());
        return new ReturnEntitySuccess(Constants.MSG_UPDATE_SUCCESS, nodeService.updateStatus(node));
    }
}
