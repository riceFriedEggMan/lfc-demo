package com.rice.lfcdemo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.rice.lfcdemo.domain.ResponseResult;
import com.rice.lfcdemo.entity.Category;


/**
 * 分类表(Category)表服务接口
 *
 * @author makejava
 * @since 2026-05-23 19:40:54
 */
public interface CategoryService extends IService<Category> {

    ResponseResult getCategoryList();

    ResponseResult pageCategory(Integer pageNo, Integer pageSize, Category category);
}
