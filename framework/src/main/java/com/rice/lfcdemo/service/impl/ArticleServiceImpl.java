package com.rice.lfcdemo.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rice.lfcdemo.constants.SystemConstants;
import com.rice.lfcdemo.domain.ResponseResult;
import com.rice.lfcdemo.entity.Article;
import com.rice.lfcdemo.entity.vo.ArticleVo;
import com.rice.lfcdemo.entity.vo.PageVo;
import com.rice.lfcdemo.mapper.ArticleMapper;
import com.rice.lfcdemo.service.ArticleService;
import com.rice.lfcdemo.service.CategoryService;
import com.rice.lfcdemo.utils.BeanCopyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 文章表(Article)表服务实现类
 *
 * @author makejava
 * @since 2026-05-23 10:34:02
 */
@Service
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article> implements ArticleService {

    @Autowired
    private CategoryService categoryService;

    @Override
    public ResponseResult articleList(Integer pageNum, Integer pageSize, Long categoryId) {
        LambdaQueryWrapper<Article> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Objects.nonNull(categoryId) && categoryId > 0, Article::getCategoryId, categoryId);
        queryWrapper.eq(Article::getStatus, SystemConstants.ARTICLE_STATUS_NORMAL);
        queryWrapper.orderByDesc(Article::getIsTop);

        Page<Article> page = new Page(pageNum, pageSize);
        page(page, queryWrapper);

        // 将categoryID改成中文描述
        page.getRecords().forEach(item -> item.setCategoryName(categoryService.getById(item.getCategoryId()).getName()));

        List<ArticleVo> articleVos = BeanCopyUtils.copyList(page.getRecords(), ArticleVo.class);

        PageVo pageVo = new PageVo(articleVos, page.getTotal());

        return ResponseResult.ok(pageVo);
    }
}
