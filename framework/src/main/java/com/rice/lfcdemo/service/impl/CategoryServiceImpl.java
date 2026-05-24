package com.rice.lfcdemo.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rice.lfcdemo.entity.Category;
import com.rice.lfcdemo.mapper.CategoryMapper;
import com.rice.lfcdemo.service.CategoryService;
import org.springframework.stereotype.Service;

/**
 * 分类表(Category)表服务实现类
 *
 * @author makejava
 * @since 2026-05-23 19:40:54
 */
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

}
