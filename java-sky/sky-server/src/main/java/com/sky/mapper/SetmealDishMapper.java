package com.sky.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SetmealDishMapper {

    // 根据菜品id获取套餐菜品id
    List<Long> getSetmealDishIds(List<Long> dishIds);
}
