package com.zhongzhou.api.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zhongzhou.api.entity.Station;
import com.zhongzhou.api.service.impl.StationServiceImpl;
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
 * 岗位 前端控制器
 * </p>
 *
 * @author wqc
 * @since 2021-06-12
 */
@RestController
@RequestMapping("/api/station")
@Slf4j
public class StationController extends BaseController {
    @Resource
    private StationServiceImpl stationService;

    /**
     * 分页查询列表
     *
     * @param pager   分页信息
     * @param station Station
     * @return ReturnEntity
     */
    @GetMapping("/page")
    public ReturnEntity selectPageList(Pager<Station> pager, Station station,
                                       HttpServletRequest request, HttpServletResponse response) {
        try {
            QueryWrapper<Station> wrapper = new QueryWrapper<>();
            wrapper.like(StringUtils.isNotBlank(station.getName()),"name" , station.getName());
            wrapper.orderByDesc("sort");
            List<Station> records = stationService.page(pager, wrapper).getRecords();
            int count = stationService.count(wrapper);
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
     * @param station Station
     * @return ReturnEntity
     */
    @GetMapping("/list")
    public ReturnEntity selectList(Station station,
                                   HttpServletRequest request, HttpServletResponse response) {
        try {
            QueryWrapper<Station> wrapper = new QueryWrapper<>();
            wrapper.like(StringUtils.isNotBlank(station.getName()),"name" , station.getName());
            wrapper.orderByDesc("sort");
            List<Station> list = stationService.list(wrapper);
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
            Station station = stationService.getById(id);
            if (null != station) {
                return new ReturnEntitySuccess(Constants.MSG_FIND_SUCCESS, station);
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
     * @param station Station
     * @param result  BindingResult
     * @return ReturnEntity
     */
    @PostMapping("/add")
    public ReturnEntity save(@Validated @RequestBody Station station, BindingResult result,
                             HttpServletRequest request, HttpServletResponse response) {
        if (result.hasErrors()) {
            FieldError fieldError = result.getFieldErrors().get(0);
            String errorMsg = fieldError.getDefaultMessage();
            if (Constants.MSG_ERROR_CANNOT_NULL.equals(errorMsg)) {
                errorMsg = fieldError.getField() + fieldError.getDefaultMessage();
            }
            return new ReturnEntityError(errorMsg, null, station);
        } else {
            try {
                QueryWrapper<Station> wrapper = new QueryWrapper<>();
                wrapper.eq("name" , station.getName());
                if (stationService.count(wrapper) > 0) {
                    return new ReturnEntityError(Constants.MSG_FIND_EXISTED, station);
                } else {
                    if (stationService.save(station)) {
                        return new ReturnEntitySuccess(Constants.MSG_INSERT_SUCCESS, station);
                    } else {
                        return new ReturnEntityError(Constants.MSG_INSERT_FAILED, station);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                log.error("[" + Constants.MSG_INSERT_FAILED + "]:{}", e.getMessage());
                return new ReturnEntityError(Constants.MSG_INSERT_FAILED, station);
            }
        }
    }

    /**
     * 修改
     *
     * @param id      主键
     * @param station Station
     * @param result  BindingResult
     * @return ReturnEntity
     */
    @PutMapping("/edit/{id}")
    public ReturnEntity updateById(@PathVariable("id") Long id, @Validated @RequestBody Station station, BindingResult result,
                                   HttpServletRequest request, HttpServletResponse response) {
        if (result.hasErrors()) {
            return new ReturnEntityError(result.getFieldErrors().get(0).getDefaultMessage(), station);
        } else {
            try {
                if (null == stationService.getById(id)) {
                    return new ReturnEntityError(Constants.MSG_FIND_NOT_FOUND, station);
                } else {
                    if (stationService.updateById(station)) {
                        return new ReturnEntitySuccess(Constants.MSG_UPDATE_SUCCESS, station);
                    } else {
                        return new ReturnEntityError(Constants.MSG_UPDATE_FAILED, station);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                log.error("[id:{} " + Constants.MSG_UPDATE_FAILED + "]:{}", id, e.getMessage());
                return new ReturnEntityError(Constants.MSG_UPDATE_FAILED, station);
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
            if (null == stationService.getById(id)) {
                return new ReturnEntityError(Constants.MSG_FIND_NOT_FOUND, id);
            } else {
                if (stationService.removeById(id)) {
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
