package com.zhongzhou.api.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zhongzhou.api.common.TokenController;
import com.zhongzhou.api.entity.*;
import com.zhongzhou.api.mapper.NodeMapper;
import com.zhongzhou.api.service.IMobileService;
import com.zhongzhou.common.utils.Constants;
import com.zhongzhou.common.utils.ServletUtils;
import com.zhongzhou.common.vo.NodeMarkVO;
import com.zhongzhou.common.vo.ReportUserVO;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务实现类
 * </p>
 */
@Service
public class MobileServiceImpl implements IMobileService {

    private static final long serialVersionUID = 2955472032639817L;
    @Resource
    DepartmentServiceImpl departmentService;
    @Resource
    SysUserServiceImpl sysUserService;
    @Resource
    PositionServiceImpl positionService;
    @Resource
    NodeMapper nodeMapper;
    @Resource
    TokenController tokenController;
    @Resource
    ReportServiceImpl reportService;
    @Resource
    NodeTimeServiceImpl nodeTimeService;


    @Override
    public List<NodeMarkVO> dateNodeList(String time) {
        //当前时间
        LocalDateTime nowTime = getLocalDateTime(time + "-01 00:00:00");
        //上个月
        LocalDateTime prevTime = nowTime.minusMonths(1);
        //下个月
        LocalDateTime nextTime = nowTime.plusMonths(1);
        //init data
        List<NodeMarkVO> dataList = new ArrayList<>();
        //query
        dataList.addAll(getTimeNodeMarkVOS(dtfYM.format(prevTime)));
        dataList.addAll(getTimeNodeMarkVOS(dtfYM.format(nowTime)));
        dataList.addAll(getTimeNodeMarkVOS(dtfYM.format(nextTime)));
        //return data
        return dataList;
    }

    /**
     * 获取指定标记节点
     *
     * @param time yyyy-MM-dd
     * @return 标记节点集合
     */
    private List<NodeMarkVO> getTimeNodeMarkVOS(String time) {
        LocalDateTime dateTime = getLocalDateTime(time + "-01 00:00:00");
        //当前月份最大天数
        int monthMaxDay = LocalDateTime.of(LocalDate.from(dateTime.with(TemporalAdjusters.lastDayOfMonth())), LocalTime.MAX).getDayOfMonth();
        //开始时间
        String startTime = time + "-01";
        //结束时间
        String endTime = time + "-" + monthMaxDay;
        //日期节点列表
        List<Node> nodeList = nodeMapper.dateNodeList(startTime, endTime);
        //获取指定月份的所有天的标记信息
        return getMonthNodeMarkList(nodeList, monthMaxDay, time);
    }

    @Override
    public Map<String, Object> personList(String time) {
        Map<String, Object> data = new HashMap<>();
        //总人数
        data.put("countPerson", queryReportCount(time));
        //领导
        data.put("leader", queryLeaderReportList(time));
        //梯队
        data.put("ranks", queryRanks(time));
        //统计梯队下的总人数
        Map<String, List<ReportUserVO>> ranks = (Map<String, List<ReportUserVO>>) data.get("ranks");
        //
        List<ReportUserVO> oneRankList = ranks.get("oneRankList");
        List<ReportUserVO> surplusRankList = ranks.get("surplusRankList");
        List<ReportUserVO> policeRankList = ranks.get("policeRankList");
        //
        data.put("oneRankListCount", rankCount(oneRankList));
        data.put("surplusRankListCount", rankCount(surplusRankList));
        data.put("policeRankListCount", rankCount(policeRankList));
        //return data
        return data;
    }

    /**
     * 查询当天的报备信息总数
     *
     * @param time yyyy-MM-dd
     * @return 总数
     */
    private int queryReportCount(String time) {
        return reportService.queryReportCount(time);
    }

    /**
     * 获取指定月份的所有天的标记信息
     *
     * @param nodeList    敏感节点集合
     * @param monthMaxDay 当月最大天数
     * @param yearMonth   yyyy-MM
     * @return 节点标记集合
     */
    private List<NodeMarkVO> getMonthNodeMarkList(List<Node> nodeList, int monthMaxDay, String yearMonth) {
        //
        List<NodeMarkVO> list = new ArrayList<>();
        //存在敏感节点
        if (nodeList != null && nodeList.size() > 0) {
            //
            for (int i = 1; i <= monthMaxDay; i++) {
                //yyyy-MM-dd HH:mm:ss
                LocalDateTime applyTime = getLocalDateTime(getYearMonthDayHMS(yearMonth, i));
                //获取指定时间的标记
                list.add(applyTimeNodeMark(nodeList, applyTime));
            }

        } else { //不存在敏感节点
            for (int i = 1; i <= monthMaxDay; i++) {
                NodeMarkVO obj = new NodeMarkVO();
                obj.setTime(getYearMonthDay(yearMonth, i));
                obj.setMarkPoint(false);
                //勤务等级：1战时（红色），2加强（黄色），3常态（正常）
                obj.setReportStatus(3);
                //collect
                list.add(obj);
            }
        }
        //自己本月备勤信息
        selfReportTime(list, yearMonth);
        //return data
        return list;
    }

    /**
     * 自己本月备勤信息
     *
     * @param list      敏感节点集合
     * @param yearMonth yyyy-MM
     */
    private void selfReportTime(List<NodeMarkVO> list, String yearMonth) {
        Long userId = tokenController.getUserId(ServletUtils.getRequest());
        //查询指定用户和年月的备勤信息
        List<Report> reportList = queryUserReportYearMonth(yearMonth, userId);
        //循环判断是否存在
        if (reportList != null && reportList.size() > 0) {
            for (Report report : reportList) {
                //获取对应天
                Integer day = Integer.valueOf(dtfDD.format(report.getCreateTime()));
                //获取对应天的节点
                NodeMarkVO nodeMarkVO = list.get(day - 1);
                nodeMarkVO.setPointText("备");
                nodeMarkVO.setMarkPoint(true);
                //勤务等级：1战时（红色），2加强（黄色），3常态（正常）
                if (nodeMarkVO.getReportStatus() == 1) {
                    nodeMarkVO.setPointTextColor("#ff4200");
                } else if (nodeMarkVO.getReportStatus() == 2) {
                    nodeMarkVO.setPointTextColor("#ff9d27");
                } else if (nodeMarkVO.getReportStatus() == 3) {
                    nodeMarkVO.setPointTextColor("#007dfe");
                }
            }
        }
        //循环所有节点赋值
        for (NodeMarkVO nodeMarkVO : list) {
            if (nodeMarkVO.getReportStatus() == 1) {
                nodeMarkVO.setText(".");
                nodeMarkVO.setMarkTextColor("#ff4200");
            } else if (nodeMarkVO.getReportStatus() == 2) {
                nodeMarkVO.setText(".");
                nodeMarkVO.setMarkTextColor("#ff9d27");
            } else if (nodeMarkVO.getReportStatus() == 3) {
                nodeMarkVO.setMarkTextColor("#007dfe");
            }
        }
        //查询指定月的节点时间
        List<NodeTime> nodeTimeList = queryNodeTimeMM(yearMonth.substring(5));
        //判断节点时间是否相同
        if (nodeTimeList != null && nodeTimeList.size() > 0) {
            for (NodeMarkVO nodeMarkVO : list) {
                for (NodeTime nodeTime : nodeTimeList) {
                    if (nodeMarkVO.getTime().equals(yearMonth + "-" + nodeTime.getDay())) {
                        List<NodeTime> timeList = nodeMarkVO.getNodeTimeList();
                        if (timeList == null) {
                            timeList = new ArrayList<>();
                        }
                        timeList.add(nodeTime);
                        nodeMarkVO.setNodeTimeList(timeList);
                    }
                }
            }
        }
    }

    /**
     * 查询指定用户和年月的备勤信息
     *
     * @param yearMonth yyyy-MM
     * @param userId    用户id
     * @return 备勤集合
     */
    List<Report> queryUserReportYearMonth(String yearMonth, Long userId) {
        QueryWrapper<Report> wrapper = new QueryWrapper<>();
        wrapper.eq("DATE_FORMAT(create_time,'%Y-%m')", yearMonth);
        wrapper.eq("create_user_id", userId);
        return reportService.list(wrapper);
    }

    /**
     * 查询指定年月的节点时间
     *
     * @param month MM
     * @return 节点时间集合
     */
    List<NodeTime> queryNodeTimeMM(String month) {
        QueryWrapper<NodeTime> wrapper = new QueryWrapper<>();
        wrapper.eq("month", month);
        return nodeTimeService.list(wrapper);
    }

    //dd
    DateTimeFormatter dtfDD = DateTimeFormatter.ofPattern("dd");
    //yyyy-MM
    DateTimeFormatter dtfYM = DateTimeFormatter.ofPattern("yyyy-MM");
    //yyyy-MM-dd
    DateTimeFormatter dtfYMD = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    //yyyy-MM-dd HH:mm:ss
    DateTimeFormatter dtfYMDHMS = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * @param nodeList  敏感节点集合
     * @param applyTime 指定时间
     * @return 节点标记
     */
    private NodeMarkVO applyTimeNodeMark(List<Node> nodeList, LocalDateTime applyTime) {

        for (Node node : nodeList) {
            LocalDateTime startTime = node.getStartTime();
            LocalDateTime endTime = node.getEndTime();
            // 指定时间等于开始时间 || 指定时间等于结束时间 || 指定时间在结束时间之前，指定时间在开始时间之后
            if (dtfYMD.format(applyTime).equals(dtfYMD.format(startTime)) || dtfYMD.format(applyTime).equals(dtfYMD.format(endTime))
                    || applyTime.isBefore(endTime) && applyTime.isAfter(startTime)) {
                NodeMarkVO obj = new NodeMarkVO();
                obj.setTime(dtfYMD.format(applyTime));
                obj.setMarkPoint(false);
                //勤务等级：1战时（红色），2加强（黄色），3常态（正常）
                obj.setReportStatus(node.getReportStatus());
                //开始时间和结束时间
                obj.setStartTime(dtfYMD.format(startTime));
                obj.setEndTime(dtfYMD.format(endTime));
                //
                return obj;
            }
        }
        //勤务等级：3常态（正常）
        NodeMarkVO obj = new NodeMarkVO();
        obj.setTime(dtfYMD.format(applyTime));
        obj.setMarkPoint(false);
        //勤务等级：1战时（红色），2加强（黄色），3常态（正常）
        obj.setReportStatus(3);
        return obj;
    }

    /**
     * @param time yyyy-MM-dd HH:mm:ss
     * @return LocalDateTime
     */
    private LocalDateTime getLocalDateTime(String time) {
        return LocalDateTime.parse(time, dtfYMDHMS);
    }

    /**
     * @param yearMonth yyyy-MM
     * @param day       天
     * @return yyyy-MM-dd
     */
    private String getYearMonthDay(String yearMonth, int day) {
        String time = null;
        if (day < 10) {
            time = yearMonth + "-0" + day;
        } else {
            time = yearMonth + "-" + day;
        }
        return time;
    }

    /**
     * @param yearMonth yyyy-MM
     * @param day       天
     * @return yyyy-MM-dd HH:mm:ss
     */
    private String getYearMonthDayHMS(String yearMonth, int day) {
        return getYearMonthDay(yearMonth, day) + " 00:00:00";
    }


    /**
     * 查询用户在指定时间是否报备
     *
     * @param sysUserList 用户集合
     * @param time        yyyy-MM-dd
     * @return 报备用户集合
     */
    List<SysUser> queryTimeReport(List<SysUser> sysUserList, String time) {
        if (sysUserList != null && sysUserList.size() > 0) {
            QueryWrapper<Report> wrapper = new QueryWrapper<>();
            wrapper.eq("DATE_FORMAT(create_time,'%Y-%m-%d')", time);
            wrapper.in("create_user_id", sysUserList.stream().map(SysUser::getId).collect(Collectors.toList()));
            List<Report> reportList = reportService.list(wrapper);
            if (reportList != null && reportList.size() > 0) {
                List<SysUser> dataList = new ArrayList<>();
                for (Report report : reportList) {
                    for (SysUser sysUser : sysUserList) {
                        if (report.getCreateUserId().equals(sysUser.getId())) {
                            dataList.add(sysUser);
                            break;
                        }
                    }
                }
                //
                return dataList;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    /**
     * 查询指定直接报备的领导
     *
     * @return 领导集合
     */
    List<Report> queryLeaderReportList(String time) {
        //查询领导部门
        List<Department> departmentList = departmentService.queryType(Constants.DEPART_TYPE_LEADER);
        if (departmentList != null && departmentList.size() > 0) {
            QueryWrapper<Report> wrapper = new QueryWrapper<>();
            wrapper.in("dep_id", departmentList.stream().map(Department::getId).collect(Collectors.toList()));
            wrapper.eq("DATE_FORMAT(create_time,'%Y-%m-%d')", time);
            return reportService.list(wrapper);
        } else {
            return null;
        }
    }


    /**
     * 查询领导职位
     *
     * @return 领导id
     */
    private List<Long> queryPositionLeader() {
        QueryWrapper<Position> wrapper = new QueryWrapper<>();
        wrapper.eq("type", Constants.POSITION_TYPE_LEADER);
        List<Position> list = positionService.list(wrapper);
        if (list != null && list.size() > 0) {
            return list.stream().map(Position::getId).collect(Collectors.toList());
        } else {
            return null;
        }
    }

    /**
     * 查询梯队
     *
     * @param time yyyy-MM-dd
     * @return 梯队
     */
    private Map<String, List<ReportUserVO>> queryRanks(String time) {
        //查看权限 默认所有
        boolean lookAll = true;
        //用户信息
        SysUser sysUser = null;
        //用户信息
        Long userId = tokenController.getUserId(ServletUtils.getRequest());
        //局领导与指挥中心人员可以看到全局的备勤信息，部门领导（部门长、教导员）也可以查看全局的备勤信息，部门副职及其他警辅人员只能查看本部门的备勤信息。
        if (userId != null) {
            sysUser = sysUserService.findDetailById(userId);
            if (sysUser != null) {
                //
                Position position = queryUserByPosition(sysUser);
                //是否能查看所有 : 0 无法查看所有 1 可以查看所有
                if (sysUser.getDepartmentList() != null && sysUser.getDepartmentList().size() > 0) {
                    for (Department department : sysUser.getDepartmentList()) {
                        if (department.getLookflag() != null && department.getLookflag() == 1) {
                            lookAll = true;
                            break;
                        }
                    }
                }
                //1局领导，2部门领导，3其他" / 民警
                if (position != null && (position.getType() == 1 || position.getType() == 2 || "民警".equals(position.getName()))) {
                    lookAll = true;
                }
            }
        }
        //查询指定部门的王发明在指定时间是否存在
        boolean zhzxFlag = false;
        List<Rota> rotaList = reportService.selectRota("值班局长", time, "王发明");
        if (rotaList != null && rotaList.size() > 0) {
            zhzxFlag = true;
        }
        //第一梯队-机关业务部门
        List<ReportUserVO> oneRankList = queryRankDetail(Constants.DEPART_TYPE_ONE_RANK, time, lookAll, sysUser, zhzxFlag);
        //2第二梯队-剩余机关业务部门
        List<ReportUserVO> surplusRankList = queryRankDetail(Constants.DEPART_TYPE_SURPLUS_RANK, time, lookAll, sysUser, zhzxFlag);
        //3增援梯队-派出所
        List<ReportUserVO> policeRankList = queryRankDetail(Constants.DEPART_TYPE_POLICE_RANK, time, lookAll, sysUser, zhzxFlag);
        //
        Map<String, List<ReportUserVO>> map = new HashMap<>();
        map.put("oneRankList", oneRankList);
        map.put("surplusRankList", surplusRankList);
        map.put("policeRankList", policeRankList);
        //
        return map;
    }

    /**
     * 统计梯队下的总人数
     *
     * @param list 梯队集合
     * @return 总数
     */
    private Integer rankCount(List<ReportUserVO> list) {
        if (list != null && list.size() > 0) {
            Integer count = 0;
            for (ReportUserVO reportUserVO : list) {
                for (ReportUserVO.InsidePosition insidePosition : reportUserVO.getPositionList()) {
                    count = count + insidePosition.getReportList().size();
                }
            }
            return count;
        } else {
            return 0;
        }
    }

    /**
     * 查询梯队指定时间的报备人员
     */
    List<ReportUserVO> queryRankDetail(int type, String time, boolean lookAll, SysUser sysUser, boolean zhzxFlag) {
        //返回数据
        List<ReportUserVO> reportUserVOList = new ArrayList<>();
        //是否存在用户或对应部门
//        if (sysUser == null) {
//            //return data
//            return reportUserVOList;
//        }
        //查询指定梯队的部门
        List<Department> departmentList = queryDepartmentType(type);
        //loop
        if (departmentList != null && departmentList.size() > 0) {
            for (Department department : departmentList) {
                //不是查询所有权限 并且 不是对应部门
//                if (!lookAll && !department.getId().equals(sysUser.getDepId())) {
//                    continue;
//                }
                //查询指定部门的勤务报备
                List<Report> reportList = reportService.queryDepartTime(department.getId(), time);
                //对应部门下的用户进行职位分组
                ReportUserVO reportUserVO = departmentReportGroupBy(department, reportList, zhzxFlag);
                if (reportUserVO != null) {
                    //添加描述
                    addDescribe(reportUserVO);
                    //collect
                    reportUserVOList.add(reportUserVO);
                }
            }
        }
        //排序
        if (reportUserVOList != null && reportUserVOList.size() > 0) {
            Collections.sort(reportUserVOList);
        }
        //return data
        return reportUserVOList;
    }

    /**
     * 添加描述
     *
     * @param reportUserVO 报备人员
     */
    private void addDescribe(ReportUserVO reportUserVO) {
        if (reportUserVO != null) {
            //
            List<ReportUserVO.InsidePosition> positionList = reportUserVO.getPositionList();
            //民警 = 民警 + 班子成员 + 文秘信息
            Map<String, String> mjMap = new HashMap<>();
            for (ReportUserVO.InsidePosition insidePosition : positionList) {
                if ("民警".equals(insidePosition.getName()) || "班子成员".equals(insidePosition.getName()) || "文秘信息".equals(insidePosition.getName())) {
                    if (insidePosition.getReportList() != null && insidePosition.getReportList().size() > 0) {
                        for (Report report : insidePosition.getReportList()) {
                            mjMap.put(report.getUserName(), report.getUserName());
                        }
                    }
                }
            }
            //辅警 = 辅警 + 通信
            Map<String, String> fjMap = new HashMap<>();
            for (ReportUserVO.InsidePosition insidePosition : positionList) {
                if ("辅警".equals(insidePosition.getName()) || "通信".equals(insidePosition.getName())) {
                    if (insidePosition.getReportList() != null && insidePosition.getReportList().size() > 0) {
                        for (Report report : insidePosition.getReportList()) {
                            fjMap.put(report.getUserName(), report.getUserName());
                        }
                    }
                }
            }
            //描述
            String describe = "( ";
            //添加 民警、辅警
            if (mjMap.size() > 0) {
                describe = describe + "民警" + mjMap.size() + "人 ";
            }
            if (fjMap.size() > 0) {
                describe = describe + "辅警" + fjMap.size() + "人 ";
            }
            //
            for (ReportUserVO.InsidePosition insidePosition : positionList) {
                //跳过
                if ("民警".equals(insidePosition.getName()) || "班子成员".equals(insidePosition.getName()) || "文秘信息".equals(insidePosition.getName()) ||
                        "辅警".equals(insidePosition.getName()) || "通信".equals(insidePosition.getName())) {
                    continue;
                }
                //
                int personCount = 0;
                if (insidePosition.getReportList() != null && insidePosition.getReportList().size() > 0) {
                    personCount = insidePosition.getReportList().size();
                }
                describe = describe + insidePosition.getName() + personCount + "人 ";
            }
            describe = describe.substring(0, describe.length() - 1) + " )";
            reportUserVO.setDescribe(describe);
        }
    }

    /**
     * 对应部门下的用户进行职位分组
     *
     * @param department 部门
     * @param reportList 报备集合
     * @return 报备人员
     */
    private ReportUserVO departmentReportGroupBy(Department department, List<Report> reportList, boolean zhzxFlag) {
        if (reportList != null && reportList.size() > 0) {
            //对职位id进行分组
            Map<String, List<Report>> collect = reportList.stream().collect(Collectors.groupingBy(Report::getPositionName));
            //职位内部类集合
            List<ReportUserVO.InsidePosition> insidePositionList = new ArrayList<>();
            for (String key : collect.keySet()) {

                //职位内部类
                ReportUserVO.InsidePosition insidePosition = new ReportUserVO.InsidePosition();
                insidePosition.setName(collect.get(key).get(0).getPositionName());
                insidePosition.setSort(collect.get(key).get(0).getPositionSort());
                insidePosition.setReportList(collect.get(key));
                //职位是班子成员，部门是情报指挥中心 ， 王发明在这一天能查到
//                if ("班子成员".equals(key) && department.getDepName().contains("情报指挥中心") && zhzxFlag) {
//                    insidePosition.setReportList(null);
//                }
                //collect
                insidePositionList.add(insidePosition);
            }
            //对职位进行排序
            Collections.sort(insidePositionList);
            //报备人员VO
            ReportUserVO reportUserVO = new ReportUserVO();
            reportUserVO.setDepId(department.getId());
            reportUserVO.setDepName(department.getDepName());
            reportUserVO.setSort(department.getSort());
            reportUserVO.setPositionList(insidePositionList);
            return reportUserVO;
        } else {
            return null;
        }
    }

    /**
     * 对应部门下的用户进行职位分组
     *
     * @param department 部门
     * @param userList   用户集合
     * @return 报备人员
     */
//    private ReportUserVO departmentUserPositionGroupBy(Department department, List<SysUser> userList) {
//        if (userList != null && userList.size() > 0) {
//            //对职位id进行分组
//            Map<String, List<SysUser>> collect = userList.stream().collect(Collectors.groupingBy(SysUser::getPositionName));
//            //职位内部类集合
//            List<ReportUserVO.InsidePosition> insidePositionList = new ArrayList<>();
//            for (String key : collect.keySet()) {
//                //根据岗位进行分组
//                addGroupStationUser(insidePositionList, collect.get(key));
//            }
//            //对职位进行排序
//            Collections.sort(insidePositionList);
//            //报备人员VO
//            ReportUserVO reportUserVO = new ReportUserVO();
//            reportUserVO.setDepId(department.getId());
//            reportUserVO.setDepName(department.getDepName());
//            reportUserVO.setPositionList(insidePositionList);
//            return reportUserVO;
//        } else {
//            return null;
//        }
//    }

    /**
     * 根据岗位进行分组
     *
     * @param insidePositionList 职位
     * @param positionUserList   相同职位用户集合
     */
//    private void addGroupStationUser(List<ReportUserVO.InsidePosition> insidePositionList, List<SysUser> positionUserList) {
//        //有岗位
//        List<SysUser> yesStationList = new ArrayList<>();
//        //没岗位
//        List<SysUser> noStationList = new ArrayList<>();
//        for (SysUser sysUser : positionUserList) {
//            if (sysUser.getStationId() != null) {
//                yesStationList.add(sysUser);
//            } else {
//                noStationList.add(sysUser);
//            }
//        }
//        //没岗位->用职位名称
//        if (noStationList.size() > 0) {
//            //职位内部类
//            ReportUserVO.InsidePosition insidePosition = new ReportUserVO.InsidePosition();
//            insidePosition.setPositionId(noStationList.get(0).getPositionId());
//            insidePosition.setName(noStationList.get(0).getPositionName());
//            insidePosition.setSort(noStationList.get(0).getPositionSort());
//            insidePosition.setSysUserList(noStationList);
//            //collect
//            insidePositionList.add(insidePosition);
//        }
//        //有岗位->用岗位名称
//        if (yesStationList.size() > 0) {
//            Map<String, List<SysUser>> stationCollect = yesStationList.stream().collect(Collectors.groupingBy(SysUser::getStationName));
//            for (String key : stationCollect.keySet()) {
//                //职位内部类
//                ReportUserVO.InsidePosition insidePosition = new ReportUserVO.InsidePosition();
//                insidePosition.setPositionId(stationCollect.get(key).get(0).getPositionId());
//                insidePosition.setName(key);
//                insidePosition.setSort(stationCollect.get(key).get(0).getStationSort());
//                insidePosition.setSysUserList(stationCollect.get(key));
//                //collect
//                insidePositionList.add(insidePosition);
//            }
//            //对职位进行排序
//            Collections.sort(insidePositionList);
//        }
//    }

    /**
     * 查询指定梯队的部门
     *
     * @param type 梯队
     * @return 部门集合
     */
    List<Department> queryDepartmentType(int type) {
        QueryWrapper<Department> wrapper = new QueryWrapper<>();
        wrapper.eq("type", type);
        wrapper.orderByAsc("id");
        return departmentService.list(wrapper);
    }

    /**
     * 查询指定职位
     *
     * @param sysUser 用户
     * @return 职位
     */
    Position queryUserByPosition(SysUser sysUser) {
        if (sysUser != null && sysUser.getPositionId() != null) {
            return positionService.getById(sysUser.getPositionId());
        } else {
            return null;
        }
    }

}
