package com.rice.lfcdemo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rice.lfcdemo.constants.SystemConstants;
import com.rice.lfcdemo.domain.ResponseResult;
import com.rice.lfcdemo.domain.enums.AppHttpCodeEnum;
import com.rice.lfcdemo.entity.Comment;

import com.rice.lfcdemo.entity.vo.CommentVo;
import com.rice.lfcdemo.exception.SystemException;
import com.rice.lfcdemo.mapper.CommentMapper;
import com.rice.lfcdemo.service.CommentService;
import com.rice.lfcdemo.service.UserService;
import com.rice.lfcdemo.utils.BeanCopyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 评论表(Comment)表服务实现类
 *
 * @author makejava
 * @since 2026-05-28 08:23:53
 */
@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {

    @Autowired
    private UserService userService;

    @Override
    public ResponseResult addComment(Comment comment) {
        if (!StringUtils.hasText(comment.getContent())) {
            throw new SystemException(AppHttpCodeEnum.CONTENT_NOT_NULL);
        }
        this.save(comment);
        return ResponseResult.ok();
    }

    @Override
    public ResponseResult commentList(String articleCommentType, Long articleId, Integer pageNum, Integer pageSize) {
        LambdaQueryWrapper<Comment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SystemConstants.ARTICLE_COMMENT.equals(articleCommentType), Comment::getArticleId, articleId);
        wrapper.eq(Comment::getType, articleCommentType);
        wrapper.eq(Comment::getRootId, -1);
        wrapper.orderByDesc(Comment::getCreateTime);
        Page<Comment> page = new Page(pageNum, pageSize);
        page(page, wrapper);
        List<Comment> records = page.getRecords();
        List<CommentVo> commentVos = new ArrayList<>();
        for (Comment comment : records) {
            CommentVo commentVo = BeanCopyUtils.copy(comment, CommentVo.class);
            List<CommentVo> commentVoList = getChildren(comment.getId());
            commentVo.setChildren(commentVoList);
            commentVos.add(commentVo);
        }
        return ResponseResult.ok(commentVos);
    }

    private List<CommentVo> getChildren(Long id) {
        LambdaQueryWrapper<Comment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Comment::getRootId, id);
        wrapper.orderByDesc(Comment::getCreateTime);
        List<Comment> comments = this.list(wrapper);

        List<CommentVo> commentVos = toCommentVoList(comments);
        return  commentVos;
    }

    private List<CommentVo> toCommentVoList(List<Comment> comments) {
        List<CommentVo> commentVos = BeanCopyUtils.copyList(comments, CommentVo.class);
        for (CommentVo commentVo : commentVos) {
            String nickName = userService.getById(commentVo.getCreateBy()).getNickName();
            commentVo.setUsername(nickName);
            if (commentVo.getToCommentUserId() != -1){
                String toCommentNickName = userService.getById(commentVo.getToCommentUserId()).getNickName();
                commentVo.setToCommentUserName(toCommentNickName);
            }
        }
        return commentVos;
    }
}
