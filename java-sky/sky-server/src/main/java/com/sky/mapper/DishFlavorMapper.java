package com.sky.mapper;

import com.sky.entity.DishFlavor;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 菜品口味 Mapper 接口
 * @author 上玄
 **/
@Mapper
public interface DishFlavorMapper {

    // 批量插入菜品口味
    void insertBatch(List<DishFlavor> flavors);

    // 根据菜品 ID 删除菜品口味
    @Delete("DELETE FROM dish_flavor WHERE dish_id = #{dishId}")
    void deleteByDishId(Long dishId);

    // 根据菜品 ID集合 批量删除菜品口味
    void deleteByDishIds(List<Long> dishIds);

    // 根据菜品 ID 查询菜品口味
    @Select("SELECT * FROM dish_flavor WHERE dish_id = #{dishId}")
    List<DishFlavor> getByDishId(Long dishId);

}
