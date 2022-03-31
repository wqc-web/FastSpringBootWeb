package com.zhongzhou.api.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhongzhou.api.entity.*;
import com.zhongzhou.api.mapper.ReportMapper;
import com.zhongzhou.api.mapper.RotaMapper;
import com.zhongzhou.api.mapper.SysUserMapper;
import com.zhongzhou.api.service.IReportService;
import com.zhongzhou.common.utils.Constants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * <p>
 * 勤务报备 服务实现类
 * </p>
 *
 * @author wqc
 * @since 2021-06-05
 */
@Service
@Slf4j
public class ReportServiceImpl extends ServiceImpl<ReportMapper, Report> implements IReportService {
    private final static String XLS = "xls";
    public static final String XLSX = "xlsx";

    @Autowired
    private SysUserServiceImpl sysUserService;
    @Resource
    private ReportServiceImpl reportService;
    @Resource
    private DepartmentServiceImpl departmentService;
    @Resource
    private PositionServiceImpl positionService;
    @Resource
    ReportMapper reportMapper;
    @Resource
    private RotaMapper rotaMapper;



    @Override
    public void importExcel(MultipartFile myFile) throws Exception {
        Workbook workbook = null;
        String fileName = myFile.getOriginalFilename();//  获取文件名
        if (fileName.endsWith(XLS)) {
            try {
                workbook = new HSSFWorkbook(myFile.getInputStream());//  2003版本

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (fileName.endsWith(XLSX)) {
            try {
                workbook = new XSSFWorkbook(myFile.getInputStream());//  2007版本
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {

        }
        //第一个sheet
        Sheet sheet = workbook.getSheet("sheet1");
        //获取工作表上的最后一行
        int lastRow = sheet.getLastRowNum();
        if (lastRow <= 2) {
            throw new NullPointerException("没有数据!");
        }
        //时间行
        Row timeRow = sheet.getRow(1);
        //开始循环 从左到右
        Row positionRow = sheet.getRow(2);
        int start = 1;
        int end = 0;
        boolean nextFlag = true;
        while (nextFlag) {
            //获取每个时间段的数据
            for (int i = start + 1; true; i++) {
                //获取班子成员
                if (positionRow.getCell(i) == null) {
                    end = i;
                    nextFlag = false;
                    break;
                }
                String positionName = positionRow.getCell(i).getStringCellValue();
                if ("班子成员".equals(positionName)) {
                    end = i;
                    break;
                    //最后一列
                } else if (positionName == null || "".equals(positionName.trim())) {
                    end = i;
                    nextFlag = false;
                    break;
                }
            }
            //开始操作数据
            operationData(sheet, start, end, positionRow, lastRow, timeRow);
            //
            start = end;
        }
    }

    @Override
    public Report querySelfReport(Long userId, String time) {
        QueryWrapper<Report> wrapper = new QueryWrapper<>();
        wrapper.eq("DATE_FORMAT(create_time,'%Y-%m-%d')", time);
        wrapper.eq("create_user_id", userId);
        List<Report> list = list(wrapper);
        if (list != null && list.size() > 0) {
            return list.get(0);
        } else {
            return null;
        }
    }

    @Override
    public List<Report> queryDepartTime(Long depId, String time) {
        return reportMapper.queryDepartTime(depId, time);
    }

    @Override
    @DS("secondary")
    public List<Rota> selectRota(String depname, String time, String name) {
        return rotaMapper.slectList(depname, time, name);
    }

    @Override
    public Integer queryReportCount(String time) {
        return reportMapper.queryReportCount(time);
    }

    /**
     * 操作数据
     *
     * @param sheet       表格
     * @param start       开始列
     * @param end         结束列
     * @param positionRow 职位行
     * @param lastRow     最后一行
     * @param timeRow     时间行
     */
    private void operationData(Sheet sheet, int start, int end, Row positionRow, int lastRow, Row timeRow) throws Exception {
        //获取时间
        Date date = timeRow.getCell(start).getDateCellValue();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String time = sdf.format(date);
        //左右
        for (int i = 3; i <= lastRow; i++) {
            Row row = sheet.getRow(i);
            getRowData(sheet, row, start, end, time, i);
        }
    }

    /**
     * @param row     行
     * @param start   开始列
     * @param end     结束列
     * @param time    时间
     * @param topDown 上下
     * @param
     */
    private void getRowData(Sheet sheet, Row row, int start, int end, String time, int topDown) throws Exception {
        //获取部门名称
        String depName = sheet.getRow(topDown).getCell(0).getStringCellValue();
        Department department = departmentService.queryLikeDepName(depName);
        if (department == null && "局备勤领导".equals(depName.trim())) {
            List<Department> departmentList = departmentService.queryType(Constants.DEPART_TYPE_LEADER);
            if (departmentList != null && departmentList.size() > 0) {
                department = departmentList.get(0);
            }
        }
        if (department == null) {
            return;
        }
        //
        for (int i = start; i < end; i++) {
            if (row.getCell(i) == null) {
                continue;
            }
            //获取职位名称
            String positionName = sheet.getRow(2).getCell(i).getStringCellValue();
            Position position = positionService.queryName(positionName);
            if (position == null) {
                return;
            }
            //
            String name = getCellValue(row.getCell(i));
            //分割名称
            String nameArr[] = splitName(name);
            if (nameArr != null) {
                //获取规则
                Map<String, Integer> map = getRule();
                addReportData(time, nameArr, department.getId(), position.getId(), map);
            }
        }
    }


    /**
     * 添加数据
     *
     * @param time    时间 yyyy-MM-dd
     * @param nameArr 名称数组
     * @param map     规则
     */
    private void addReportData(String time, String[] nameArr, Long depId, Long positionId, Map<String, Integer> map) {
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime createTime = LocalDateTime.parse(time + " 00:00:00", df);
        List<Report> reportList = new ArrayList<>();
        for (int i = 0; i < nameArr.length; i++) {
            String name = nameArr[i];
            //名称是空
            if (StringUtils.isBlank(name)) {
                continue;
            }
            //根据真实姓名查询
            SysUser sysUser = sysUserService.queryRealName(name);
            //判断当天是否存在相同用户报备
            if (existUserNameInDepIdInPositionInTime(name, depId, positionId, time)) {
                continue;
            }
            //new
            Report report = new Report();
            report.setDrinkWineStatus(map.get("drinkWineStatus"));
            report.setWorkStatus(map.get("workStatus"));
            report.setCommentStatus(map.get("commentStatus"));
            report.setCreateTime(createTime);
            if (sysUser != null) {
                report.setCreateUserId(sysUser.getId());
            }
            report.setUserName(name.trim());
            report.setDepId(depId);
            report.setPositionId(positionId);
            //collect
            reportList.add(report);
        }
        //有数据添加
        if (reportList.size() > 0) {
            //insert db
            reportService.saveBatch(reportList);
        }
    }

    /**
     * 判断当天是否存在相同用户报备
     *
     * @param name       用户名称
     * @param time       时间
     * @param depId      部门id
     * @param positionId 职位id
     * @return
     */
    private boolean existUserNameInDepIdInPositionInTime(String name, Long depId, Long positionId, String time) {
        QueryWrapper<Report> wrapper = new QueryWrapper<>();
        wrapper.eq("DATE_FORMAT(create_time,'%Y-%m-%d')", time);
        wrapper.eq("username", name);
        wrapper.eq("dep_id", depId);
        wrapper.eq("position_id", positionId);
        return count(wrapper) > 0;
    }

    /**
     * 获取规则
     *
     * @return 规则
     */
    private Map<String, Integer> getRule() throws Exception {
        //TODO
        return new HashMap<>();
//        throw new Exception("代码未实现");
    }

    /**
     * 分割名称
     *
     * @param name 名称
     * @return 多个名称
     */
    private String[] splitName(String name) {
        //判断是否有名称
        if (name == null || "".equals(name.trim())) {
            return null;
        } else { //有
            if (name.contains("、")) {
                return name.split("、");
            }
            if (name.contains(",")) {
                return name.split(",");
            }
            if (name.contains("，")) {
                return name.split("，");
            }
            //default
            return name.split("、");
        }
    }

    /**
     * 获取单元格的值
     *
     * @param cell
     * @return
     */
    public static String getCellValue(Cell cell) {

        if (cell == null)
            return "";

        if (cell.getCellType() == CellType.STRING) {

            return cell.getStringCellValue();

        } else if (cell.getCellType() == CellType.BOOLEAN) {

            return String.valueOf(cell.getBooleanCellValue());

        } else if (cell.getCellType() == CellType.FORMULA) {

            return cell.getCellFormula();

        } else if (cell.getCellType() == CellType.NUMERIC) {
            return String.valueOf(cell.getNumericCellValue());
        }
        return "";
    }
}
