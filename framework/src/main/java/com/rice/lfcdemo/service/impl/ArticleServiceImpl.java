package com.rice.lfcdemo.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rice.lfcdemo.constants.SystemConstants;
import com.rice.lfcdemo.entity.ArticleTag;
import com.rice.lfcdemo.entity.vo.ArticleByIdVo;
import com.rice.lfcdemo.entity.vo.ArticleDetailVo;
import com.rice.lfcdemo.entity.vo.HotArticleVo;
import com.rice.lfcdemo.domain.ResponseResult;
import com.rice.lfcdemo.entity.Article;
import com.rice.lfcdemo.entity.Category;
import com.rice.lfcdemo.entity.dto.ArticleDto;
import com.rice.lfcdemo.entity.vo.ArticleVo;
import com.rice.lfcdemo.entity.vo.PageVo;
import com.rice.lfcdemo.mapper.ArticleMapper;
import com.rice.lfcdemo.mapper.CategoryMapper;
import com.rice.lfcdemo.service.ArticleService;
import com.rice.lfcdemo.service.ArticleTagService;
import com.rice.lfcdemo.service.CategoryService;
import com.rice.lfcdemo.utils.BeanCopyUtils;
import com.rice.lfcdemo.utils.RedisCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
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
    @Autowired
    private RedisCache redisCache;
    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private ArticleTagService articleTagService;

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
    @Transactional(rollbackFor = Exception.class)
    public ResponseResult add(ArticleDto articleDto) {
        Long count = redisCache.getCacheMapValue("article:viewCount", articleDto.getId().toString());
        if (Objects.isNull(count)) {
            redisCache.setCacheMapValue("article:viewCount", articleDto.getId().toString(), articleDto.getViewCount());
        }
        Article article = BeanCopyUtils.copy(articleDto, Article.class);
        this.save(article);

        // 增加标签
        List<Long> tags = articleDto.getTags();
        List<ArticleTag> articleTagList = tags.stream()
                .map(tag -> new ArticleTag(article.getId(), tag)).collect(Collectors.toList());
        articleTagService.saveBatch(articleTagList);
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

        Integer viewCount = redisCache.getCacheMapValue("article:viewCount", id.toString());
        Integer countInteger = Optional.ofNullable(viewCount).orElse(0);

        article.setViewCount(countInteger.longValue());

        ArticleDetailVo detailVo = BeanCopyUtils.copy(article, ArticleDetailVo.class);

        Category category = categoryService.getById(id);

        if (null != category) {
            detailVo.setCategoryName(category.getName());
        }
        return ResponseResult.ok(detailVo);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
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

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseResult deleteArticle(Long id) {
        Article article = getById(id);
        if (Objects.isNull(article)) {
            return ResponseResult.errorResult(404, "文章不存在");
        }
        // 删除Redis中的浏览量缓存
        redisCache.deleteCacheMapValue("article:viewCount", id.toString());
        // 删除文章
        removeById(id);

        LambdaQueryWrapper<ArticleTag> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ArticleTag::getArticleId, id);
        articleTagService.remove(wrapper);

        return ResponseResult.ok();
    }

    @Override
    public PageVo selectArticlePage(Article article, Integer pageNum, Integer pageSize) {
        LambdaQueryWrapper<Article> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StringUtils.hasText(article.getTitle()), Article::getTitle, article.getTitle());
        wrapper.eq(StringUtils.hasText(article.getSummary()), Article::getSummary, article.getSummary());
        Page<Article> page = new Page<>();
        page.setCurrent(pageNum);
        page.setSize(pageSize);
        page(page, wrapper);
        List<Article> articles = page.getRecords();
        List<ArticleVo> articleVos = BeanCopyUtils.copyList(articles, ArticleVo.class);
        return new PageVo(articleVos, page.getTotal());
    }

    @Override
    public ResponseResult edit(ArticleDto articleDto) {
        Article article = BeanCopyUtils.copy(articleDto, Article.class);
        updateById(article);

        LambdaQueryWrapper<ArticleTag> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ArticleTag::getArticleId, article.getId());
        articleTagService.remove(wrapper);

        List<ArticleTag> articleTagList = articleDto.getTags().stream()
                .map(tagId -> new ArticleTag(article.getId(), tagId)).collect(Collectors.toList());

        articleTagService.saveBatch(articleTagList);
        return ResponseResult.ok();
    }
}
