package com.rice.lfcdemo.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rice.lfcdemo.constants.SystemConstants;
import com.rice.lfcdemo.domain.blog.Dto.AddArticleDto;
import com.rice.lfcdemo.domain.blog.Vo.ArticleByIdVo;
import com.rice.lfcdemo.domain.blog.Vo.ArticleDetailVo;
import com.rice.lfcdemo.domain.blog.Vo.HotArticleVo;
import com.rice.lfcdemo.domain.ResponseResult;
import com.rice.lfcdemo.entity.Article;
import com.rice.lfcdemo.entity.Category;
import com.rice.lfcdemo.entity.vo.ArticleVo;
import com.rice.lfcdemo.entity.vo.PageVo;
import com.rice.lfcdemo.mapper.ArticleMapper;
import com.rice.lfcdemo.mapper.CategoryMapper;
import com.rice.lfcdemo.service.ArticleService;
import com.rice.lfcdemo.service.CategoryService;
import com.rice.lfcdemo.utils.BeanCopyUtils;
import com.rice.lfcdemo.utils.RedisCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

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
    @Autowired
    private RedisCache redisCache;
    @Autowired
    private CategoryMapper categoryMapper;

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

    @Override
    public ResponseResult add(AddArticleDto articleDto) {
        Long count = redisCache.getCacheMapValue("article:viewCount", articleDto.getId().toString());
        if (Objects.isNull(count)) {
            redisCache.setCacheMapValue("article:viewCount", articleDto.getId().toString(), articleDto.getViewCount());
        }
        Article article = BeanCopyUtils.copy(articleDto, Article.class);
        this.save(article);

        // todo 增加标签

        return ResponseResult.ok();
    }

    @Override
    public ArticleByIdVo getInfo(Long id) {
        LambdaQueryWrapper<Article> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Article::getId, id);
        Article article = this.getById(queryWrapper);
        return BeanCopyUtils.copy(article, ArticleByIdVo.class);
    }

    @Override
    public ResponseResult getArticleDetail(Long id) {
        Article article = getById(id);

        Long viewCount = redisCache.getCacheMapValue("article:viewCount", id.toString());
        Long count = Optional.ofNullable(viewCount).orElse(0L);

        article.setViewCount(count);

        ArticleDetailVo detailVo = BeanCopyUtils.copy(article, ArticleDetailVo.class);

        Category category = categoryService.getById(id);

        if (null != category) {
            detailVo.setCategoryName(category.getName());
        }
        return ResponseResult.ok(detailVo);
    }

    @Override
    public ResponseResult updateViewCount(Long id) {
        long res = redisCache.incrementCacheMapValue("article:viewCount", id.toString(), 1L);
        if (res % 10 == 0){
            LambdaUpdateWrapper<Article> wrapper = new LambdaUpdateWrapper<>();
            wrapper.eq(Article::getId, id);
            wrapper.set(Article::getViewCount, res);
            this.update(wrapper);
        }
        return ResponseResult.ok();
    }

    @Override
    public ResponseResult hotArticles() {
        LambdaQueryWrapper<Article> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Article::getStatus, SystemConstants.ARTICLE_STATUS_NORMAL);
        queryWrapper.orderByDesc(Article::getViewCount);
        Page<Article> page = new Page<>(1, 10);
        page(page, queryWrapper);
        List<Article> records = page.getRecords();

        for (Article article : records) {
            Long count = redisCache.getCacheMapValue("article:viewCount", article.getId().toString());
            article.setViewCount(count);
        }

        List<HotArticleVo> hotArticleVos = new ArrayList<>();
        for (Article article : records) {
            HotArticleVo hotArticleVo = BeanCopyUtils.copy(article, HotArticleVo.class);
            hotArticleVos.add(hotArticleVo);
        }
        return ResponseResult.ok(hotArticleVos);
    }
}
