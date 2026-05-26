package com.rice.lfcdemo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.rice.lfcdemo.domain.blog.Dto.AddArticleDto;
import com.rice.lfcdemo.domain.blog.Vo.ArticleByIdVo;
import com.rice.lfcdemo.domain.ResponseResult;
import com.rice.lfcdemo.entity.Article;


/**
 * 文章表(Article)表服务接口
 *
 * @author makejava
 * @since 2026-05-23 10:34:02
 */
public interface ArticleService extends IService<Article> {

    ResponseResult articleList(Integer pageNum, Integer pageSize, Long categoryId);

    ResponseResult add(AddArticleDto articleDto);

    ArticleByIdVo getInfo(Long id);

    ResponseResult getArticleDetail(Long id);

    ResponseResult updateViewCount(Long id);

    ResponseResult hotArticles();
}
