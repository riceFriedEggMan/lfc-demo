package com.rice.lfcdemo.controller;


import com.rice.lfcdemo.entity.vo.ArticleByIdVo;
import com.rice.lfcdemo.domain.ResponseResult;
import com.rice.lfcdemo.entity.Article;
import com.rice.lfcdemo.entity.dto.ArticleDto;
import com.rice.lfcdemo.entity.vo.PageVo;
import com.rice.lfcdemo.service.ArticleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/article")
public class ArticleController {

    @Autowired
    private ArticleService articleService;

    @GetMapping("/{id}")
    public ResponseResult getInfo(@PathVariable(value = "id") Long id){
        ArticleByIdVo article = articleService.getInfo(id);
        return ResponseResult.ok(article);
    }

    @PostMapping("add")
    @Operation(summary = "添加文章")
    public ResponseResult add(@RequestBody ArticleDto articleDto){
        return articleService.add(articleDto);
    }

    @GetMapping("/list")
    public ResponseResult selectArticlePage(Article article, Integer pageNum, Integer pageSize){
        PageVo pageVo = articleService.selectArticlePage(article, pageNum, pageSize);
        return ResponseResult.ok(pageVo);
    }

    @PutMapping("/edit")
    public ResponseResult edit(@RequestBody ArticleDto articleDto){
        return articleService.edit(articleDto);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除文章")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "删除成功"),
            @ApiResponse(responseCode = "401", description = "未登录"),
            @ApiResponse(responseCode = "403", description = "无权限"),
            @ApiResponse(responseCode = "404", description = "文章不存在")
    })
    public ResponseResult deleteArticle(@PathVariable(value = "id") Long id) {
        return articleService.deleteArticle(id);
    }
}
