package com.zhongzhou.api.controller;

import com.zhongzhou.api.entity.Node;
import com.zhongzhou.api.service.impl.MobileServiceImpl;
import com.zhongzhou.common.base.BaseController;
import com.zhongzhou.common.bean.ReturnEntity;
import com.zhongzhou.common.bean.ReturnEntityError;
import com.zhongzhou.common.bean.ReturnEntitySuccess;
import com.zhongzhou.common.utils.Constants;
import com.zhongzhou.common.vo.NodeMarkVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 前端控制器--移动端
 * </p>
 */
@RestController
@RequestMapping("/api/mobile")
@Slf4j
public class MobileController extends BaseController {

    @Resource
    private MobileServiceImpl mobileService;

    /**
     * 日期节点列表
     *
     * @param time yyyy-MM
     * @return ReturnEntity
     */
    @GetMapping("/dateNodeList")
    public ReturnEntity dateNodeList(String time,
                                     HttpServletRequest request, HttpServletResponse response) {
        try {
            List<NodeMarkVO> nodeList = mobileService.dateNodeList(time);
            return new ReturnEntitySuccess(Constants.MSG_FIND_SUCCESS, nodeList);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("[" + Constants.MSG_FIND_FAILED + "]:" + e.getMessage());
            return new ReturnEntityError(Constants.MSG_FIND_FAILED, null, null);
        }
    }

    /**
     * 人员列表
     *
     * @param time yyyy-MM-dd
     * @return ReturnEntity
     */
    @GetMapping("/personList")
    public ReturnEntity personList(String time,
                                   HttpServletRequest request, HttpServletResponse response) {
        try {
            Map<String, Object> records = mobileService.personList(time);
            return new ReturnEntitySuccess(Constants.MSG_FIND_SUCCESS, records);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("[" + Constants.MSG_FIND_FAILED + "]:" + e.getMessage());
            return new ReturnEntityError(Constants.MSG_FIND_FAILED, null, null);
        }
    }

}
