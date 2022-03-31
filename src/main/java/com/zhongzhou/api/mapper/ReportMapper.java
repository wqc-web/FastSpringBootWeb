package com.zhongzhou.api.mapper;

import com.zhongzhou.api.entity.Report;
import com.zhongzhou.common.base.BaseDao;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 勤务报备 Mapper 接口
 * </p>
 *
 * @author wqc
 * @since 2021-06-05
 */
public interface ReportMapper extends BaseDao<Report> {

    List<Report> selectReportList(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);


    List<Report> selectexist(LocalDateTime applicationTime, Long createUserId);

    /**
     * @param depId
     * @param time
     * @return
     */
    List<Report> queryDepartTime(@Param("depId") Long depId, @Param("time") String time);

    /**
     * 查询当天的报备信息总数
     *
     * @param time yyyy-MM-dd
     * @return 总数
     */
    Integer queryReportCount(@Param("time") String time);

}
