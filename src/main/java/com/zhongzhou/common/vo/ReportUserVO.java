package com.zhongzhou.common.vo;

import com.zhongzhou.api.entity.Report;
import lombok.Data;

import java.util.List;

/**
 * 报备人员
 */
@Data
public class ReportUserVO implements Comparable<ReportUserVO> {

    /**
     * 部门id
     */
    private Long depId;
    /**
     * 部门名称
     */
    private String depName;

    /**
     * 职位集合
     */
    private List<InsidePosition> positionList;

    /**
     * 描述
     */
    private String describe;

    /**
     * 排序
     */
    private Long sort;

    @Override
    public int compareTo(ReportUserVO that) {
        if (this.sort < that.sort) {
            return 1;
        } else if (this.sort > that.sort) {
            return -1;
        } else {
            return 0;
        }
    }

    /**
     * 职位
     */
    @Data
    public static class InsidePosition implements Comparable<InsidePosition> {
        /**
         * 排序
         */
        private Long sort;
        /**
         * 职位名称
         */
        private String name;
        /**
         * 报备集合
         */
        private List<Report> reportList;

        @Override
        public int compareTo(InsidePosition that) {
            if (this.sort < that.sort) {
                return 1;
            } else if (this.sort > that.sort) {
                return -1;
            } else {
                return 0;
            }
        }
    }
}
