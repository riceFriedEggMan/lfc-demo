package com.rice.lfcdemo.controller;


import com.rice.lfcdemo.domain.blog.Vo.ArticleByIdVo;
import com.rice.lfcdemo.domain.ResponseResult;
import com.rice.lfcdemo.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ArticleController {

    @Autowired
    private ArticleService articleService;

    @GetMapping("/{id}")
    public ResponseResult getInfo(@PathVariable(value = "id") Long id){
        ArticleByIdVo article = articleService.getInfo(id);
        return ResponseResult.ok(article);
    }
}
