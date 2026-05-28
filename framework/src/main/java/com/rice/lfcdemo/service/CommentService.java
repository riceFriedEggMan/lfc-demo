package com.rice.lfcdemo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.rice.lfcdemo.domain.ResponseResult;
import com.rice.lfcdemo.entity.Comment;


/**
 * 评论表(Comment)表服务接口
 *
 * @author makejava
 * @since 2026-05-28 08:23:53
 */
public interface CommentService extends IService<Comment> {

    ResponseResult addComment(Comment comment);

    ResponseResult commentList(String articleCommentType, Long articleId, Integer pageNum, Integer pageSize);
}
