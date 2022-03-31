package com.zhongzhou.api.mapper;

import com.zhongzhou.api.entity.Report;
import com.zhongzhou.api.entity.ReportSwap;
import com.zhongzhou.common.base.BaseDao;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 勤务报备对调 Mapper 接口
 * </p>
 *
 * @author wqc
 * @since 2021-06-05
 */
public interface ReportSwapMapper extends BaseDao<ReportSwap> {

    List<ReportSwap> queryPage(@Param("page") Long startPage, @Param("size") Long size, @Param("reportSwap") ReportSwap reportSwap);

    Integer queryCount(ReportSwap reportSwap);

    ReportSwap messagePush(Long userId);

    List<Report> oneMonthWeek(@Param("userId")String userId, @Param("today") String today, @Param("weekday")String weekday);
}
