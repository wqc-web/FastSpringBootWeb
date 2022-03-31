package com.zhongzhou.api.service;

import com.zhongzhou.api.entity.Report;
import com.zhongzhou.api.entity.Rota;
import com.zhongzhou.common.base.BaseService;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * <p>
 * 勤务报备 服务类
 * </p>
 *
 * @author wqc
 * @since 2021-06-05
 */
public interface IReportService extends BaseService<Report> {

    /**
     * 导入
     *
     * @param myFile 文件流
     * @return 总数
     * @throws Exception
     */
    void importExcel(MultipartFile myFile) throws Exception;

    /**
     * 查询指定用户对应时间的报备
     *
     * @param userId 用户id
     * @param time   yyyy-MM-dd
     * @return 报备
     */
    Report querySelfReport(Long userId, String time);

    /**
     * @param depId 部门id
     * @param time  yyyy-MM-dd
     * @return 报备集合
     */
    List<Report> queryDepartTime(Long depId, String time);

    List<Rota> selectRota(String depname, String time, String name);

    /**
     * 查询当天的报备信息总数
     *
     * @param time yyyy-MM-dd
     * @return 总数
     */
    Integer queryReportCount(String time);
}
