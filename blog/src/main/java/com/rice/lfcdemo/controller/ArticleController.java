package com.rice.lfcdemo.controller;



import com.rice.lfcdemo.annotation.SystemLog;
import com.rice.lfcdemo.domain.blog.Dto.AddArticleDto;
import com.rice.lfcdemo.domain.ResponseResult;
import com.rice.lfcdemo.service.ArticleService;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/article")
@Tag(name = "01-文章管理", description = "文章的增删改查接口")
public class ArticleController {

    @Autowired
    private ArticleService articleService;

    @PostMapping("add")
    @Operation(summary = "添加文章")
    public ResponseResult add(@RequestBody AddArticleDto articleDto){
        return articleService.add(articleDto);
    }

    @GetMapping("/{id}")
    @SystemLog(bussinessName = "查看文章")
    public ResponseResult getArticleDetail(@PathVariable(value = "id") Long id){
        return articleService.getArticleDetail(id);
    }

    @PutMapping("/updateViewCount/{id}")
    public ResponseResult updateViewCount(@PathVariable(value = "id") Long id){
        return articleService.updateViewCount(id);
    }

    @GetMapping("/hotArticles")
    public ResponseResult hotArticles(){
        return articleService.hotArticles();
    }

    @GetMapping("articleList")
    @Operation(summary = "获取文章列表", description = "支持分页查询和按分类筛选")
    @Parameters({
            @Parameter(name = "pageNum", description = "页码", example = "1", required = false),
            @Parameter(name = "pageSize", description = "每页条数", example = "10", required = false),
            @Parameter(name = "categoryId", description = "分类ID", example = "10", required = false)
    })
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "查询成功"),
            @ApiResponse(responseCode = "401", description = "未登录"),
            @ApiResponse(responseCode = "403", description = "无权限")
    })
    public ResponseResult articleList(Integer pageNum,
                                      Integer pageSize,
                                      Long categoryId) {
        return articleService.articleList(pageNum, pageSize, categoryId);
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
