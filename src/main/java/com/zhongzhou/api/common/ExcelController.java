package com.zhongzhou.api.common;

import com.zhongzhou.api.service.impl.ExcelServiceImpl;
import com.zhongzhou.common.bean.ReturnEntity;
import com.zhongzhou.common.bean.ReturnEntityError;
import com.zhongzhou.common.bean.ReturnEntitySuccess;
import com.zhongzhou.common.dto.UserExcelDTO;
import com.zhongzhou.common.utils.Constants;
import com.zhongzhou.common.utils.EasyPoiUtil;
import com.zhongzhou.common.utils.FileDownloadUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping("/api/excel")
@Slf4j
public class ExcelController {

    @Resource
    private ExcelServiceImpl excelService;

    /**
     * 导入
     *
     * @param file 导入文件
     * @return ReturnEntity
     */
    @PostMapping("/importUser")
    public ReturnEntity importUser(@RequestParam("file") MultipartFile file) {
        try {
            List<UserExcelDTO> list = EasyPoiUtil.importExcel(file, 0, 1, UserExcelDTO.class);
            excelService.importSysUser(list);
            log.info(Constants.MSG_UPLOAD_SUCCESS + ", size:{}", list.size());
            return new ReturnEntitySuccess(Constants.MSG_UPLOAD_SUCCESS);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("导入用户 :{}", e.getMessage());
            return new ReturnEntityError(e.getMessage());
        }
    }

    /**
     * 下载用户导入模板
     *
     * @param request  HttpServletRequest
     * @param response HttpServletResponse
     */
    @GetMapping("/downUserTemplate")
    public void downUserTemplate(HttpServletRequest request, HttpServletResponse response) {
        try {
            String filePath = "/home/template/ServiceReportWeb";
            String fileName = "用户基本信息.xls";
            FileDownloadUtil.download(filePath, fileName, request, response);
            log.info("用户基本信息下载成功");
        } catch (Exception e) {
            e.printStackTrace();
            log.error("用户基本信息下载成功");
            throw new RuntimeException("用户基本信息下载成功");
        }
    }

}
