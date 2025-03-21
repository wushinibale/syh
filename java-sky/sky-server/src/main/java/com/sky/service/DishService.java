package com.sky.service;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.vo.DishVO;

import java.util.List;

public interface DishService {

    /**
     * 新增菜品信息
     * @param dishDTO
     */
    void saveWithFlavor(DishDTO dishDTO);

    /**
     * 分页查询菜品信息
     * @param dishPageQueryDTO
     * @return
     */
    PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO);

    // 批量删除菜品
    void deleteBatch(List<Long> ids);

    // 根据id查询菜品
    DishVO getByIdWithFlavor(Long id);

    // 修改菜品
    void updateWithFlavor(DishDTO dishDTO);

//
//    void updateEmployeeStatus(Integer status,Long id);
//
//    Employee getById(Long id);
//
//    void updateEmployee(EmployeeDTO employeeDTO);
}
