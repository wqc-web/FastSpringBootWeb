package com.zhongzhou.api.service;

import com.zhongzhou.api.entity.ReportSwap;
import com.zhongzhou.api.entity.SysUser;
import com.zhongzhou.common.base.BaseService;
import com.zhongzhou.common.base.Pager;

import java.util.List;

/**
 * <p>
 * 勤务报备对调 服务类
 * </p>
 *
 * @author wqc
 * @since 2021-06-05
 */
public interface IReportSwapService extends BaseService<ReportSwap> {

    /**
     * 分页查询
     *
     * @param pager      分页
     * @param reportSwap 勤务报备
     * @return List<ReportSwap>
     */
    List<ReportSwap> queryPage(Pager<ReportSwap> pager, ReportSwap reportSwap);

    /**
     * 查询总数
     *
     * @param reportSwap 勤务报备
     * @return 总数
     */
    Integer queryCount(ReportSwap reportSwap);

    /**
     * 查询对应部门交换的用户
     *
     * @param id       用户id
     * @param selfTime 自己的时间
     * @param swapTime 交换时间
     * @return 用户集合
     */
    List<SysUser> selectDepPepByPreId(Long id, String selfTime, String swapTime);

    Integer operationReport(Long id, Integer operation);

    ReportSwap messagePush(Long userId);
    /**
     * 添加对调
     *
     * @param reportSwap 对调
     * @return true成功 false失败
     */

}
