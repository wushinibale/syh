package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author 上玄
 * @since 2025-03-16
 */
@Service
public class DishServiceImpl implements DishService {

    @Resource
    private DishMapper dishMapper;
    @Resource
    private DishFlavorMapper dishFlavorMapper;
    @Resource
    private SetmealDishMapper setmealDishMapper;

    /**
     * 修改菜品及其口味信息
     * @param dishDTO
     */
    @Override
    public void updateWithFlavor(DishDTO dishDTO) {
        // 更新菜品信息
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        dishMapper.update(dish);
        // 删除原有的口味信息
        dishFlavorMapper.deleteByDishId(dishDTO.getId());
        // 保存新的口味信息
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if (flavors != null && flavors.size() > 0) {
            flavors.forEach(flavor -> {
                flavor.setDishId(dishDTO.getId());
            });
            // 向口味表中插入n条
            dishFlavorMapper.insertBatch(flavors);
        }
    }

    /**
     * 根据id查询菜品
     *
     * @param id 菜品id
     * @return 菜品列表
     */
    @Override
    public DishVO getByIdWithFlavor(Long id) {
        // 根据id查询菜品
        Dish dish = dishMapper.getById(id);
        // 根据id查询菜品口味
        List<DishFlavor> flavors = dishFlavorMapper.getByDishId(id);
        // 封装到vo中
        DishVO dishVO = new DishVO();
        BeanUtils.copyProperties(dish, dishVO);
        dishVO.setFlavors(flavors);
        return dishVO;
    }

    /**
     * 保存菜品及其口味信息
     *
     * @param dishDTO 菜品信息
     */
    @Override
    @Transactional
    public void saveWithFlavor(DishDTO dishDTO) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        // 向菜品表中保存1条菜品信息
        dishMapper.insert(dish);
        // 获取insert后的主键值
        Long dishId = dish.getId();
        // 向菜品口味表插入n条
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if (flavors != null && flavors.size() > 0) {
            // 给口味信息设置菜品id
            flavors.forEach(flavor -> {
                flavor.setDishId(dishId);
            });
            dishFlavorMapper.insertBatch(flavors);
        }
    }

    /**
     * 分页查询菜品信息
     *
     * @param dishPageQueryDTO
     * @return
     */
    @Override
    public PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO) {
        PageHelper.startPage(dishPageQueryDTO.getPage(), dishPageQueryDTO.getPageSize());
        Page<DishVO> page = dishMapper.pageQuery(dishPageQueryDTO);
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 批量删除菜品信息
     *
     * @param ids 菜品id列表
     */
    @Override
    @Transactional
    public void deleteBatch(List<Long> ids) {
        // 判断是否能删除(起售中状态的菜品不能删除)
        ids.forEach(id -> {
            Dish dish = dishMapper.getById(id);
            if (dish.getStatus() == StatusConstant.ENABLE) {
                // 当前菜品状态为起售中，不能删除
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
            }
        });
        // 判断是否有菜品被套餐关联
        List<Long> setmealDishIds = setmealDishMapper.getSetmealDishIds(ids);
        if (setmealDishIds != null && setmealDishIds.size() > 0) {
            // 有菜品被套餐关联，不能删除
            throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        }

        // 删除菜品及其口味信息
//        ids.forEach(id -> {
//            dishMapper.deleteById(id);
//            // 根据菜品id删除菜品口味信息
//            dishFlavorMapper.deleteByDishId(id);
//        });

        // 优化sql:sql运行次数减少，提升性能
        // 根据菜品id集合批量删除菜品及其口味信息
        // sql: delete from dish_flavor where dish_id in (ids)
        // sql: delete from dish where id in (ids)
        dishMapper.deleteByIds(ids);
        dishFlavorMapper.deleteByDishIds(ids);
    }
}
