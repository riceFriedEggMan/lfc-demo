package com.rice.lfcdemo.controller;

import com.rice.lfcdemo.constants.SystemConstants;
import com.rice.lfcdemo.domain.ResponseResult;
import com.rice.lfcdemo.entity.Comment;
import com.rice.lfcdemo.entity.dto.AddCommentDto;
import com.rice.lfcdemo.service.CommentService;
import com.rice.lfcdemo.utils.BeanCopyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/comment")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @PostMapping("/addComment")
    public ResponseResult addComment(AddCommentDto addCommentDto) {
        Comment comment = BeanCopyUtils.copy(addCommentDto, Comment.class);
        return commentService.addComment(comment);
    }

    @GetMapping("/commentList")
    public ResponseResult commentList(Long articleId, Integer pageNum, Integer pageSize) {
        return commentService.commentList(SystemConstants.ARTICLE_COMMENT, articleId, pageNum, pageSize);
    }

    @GetMapping("/linkCommentList")
    public ResponseResult linkCommentList(Long articleId, Integer pageNum, Integer pageSize) {
        return commentService.commentList(SystemConstants.LINK_COMMENT, articleId, pageNum, pageSize);
    }



}
