package com.zhongzhou.api.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.zhongzhou.api.entity.Rota;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface RotaMapper {
    @DS("secondary")
    List<Rota> slectList(@Param("deptname") String deptname, @Param("time") String time, @Param("name") String name);
}
