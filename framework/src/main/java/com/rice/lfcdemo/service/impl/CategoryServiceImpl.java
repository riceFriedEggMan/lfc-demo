package com.rice.lfcdemo.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rice.lfcdemo.constants.SystemConstants;
import com.rice.lfcdemo.domain.ResponseResult;
import com.rice.lfcdemo.entity.Article;
import com.rice.lfcdemo.entity.Category;
import com.rice.lfcdemo.entity.vo.CategoryVo;
import com.rice.lfcdemo.entity.vo.PageVo;
import com.rice.lfcdemo.mapper.CategoryMapper;
import com.rice.lfcdemo.service.ArticleService;
import com.rice.lfcdemo.service.CategoryService;
import com.rice.lfcdemo.utils.BeanCopyUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 分类表(Category)表服务实现类
 *
 * @author makejava
 * @since 2026-05-23 19:40:54
 */
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Autowired
    private ArticleService articleService;

    @Override
    public ResponseResult getCategoryList() {
        LambdaQueryWrapper<Article> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Article::getStatus, SystemConstants.ARTICLE_STATUS_NORMAL);
        List<Article> articles = articleService.list(wrapper);
        Set<Long> idSet = articles.stream().map(Article::getCategoryId).collect(Collectors.toSet());
        List<Category> categories = listByIds(idSet);
        List<Category> categoryList = categories.stream()
                .filter(category -> SystemConstants.STATUS_NORMAL.equals(category.getStatus()))
                .collect(Collectors.toList());
        List<CategoryVo> categoryVos = BeanCopyUtils.copyList(categoryList, CategoryVo.class);
        return ResponseResult.ok(categoryVos);
    }

    @Override
    public ResponseResult pageCategory(Integer pageNo, Integer pageSize, Category category) {
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(category.getName()), Category::getName, category.getName());
        wrapper.eq(StringUtils.hasText(category.getStatus()),Category::getStatus, category.getStatus());
        Page<Category> page = new Page<>(pageNo, pageSize);
        page(page, wrapper);
        PageVo pageVo = new PageVo(page.getRecords(), page.getTotal());
        return ResponseResult.ok(pageVo);
    }
}
