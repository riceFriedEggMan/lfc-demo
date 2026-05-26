package com.rice.lfcdemo.entity;

import java.util.Date;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
/**
 * 文章表(Article)表实体类
 *
 * @author makejava
 * @since 2026-05-23 10:34:02
 */
@SuppressWarnings("serial")
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("article")
@Schema(description = "文章信息表")
public class Article  {
    @TableId
    @Schema(description = "文章ID", example = "1")
    private Long id;

    @Schema(description = "标题", example = "Spring Boot入门教程", required = true)
    private String title;

    @Schema(description = "文章内容", required = true)
    private String content;

    @Schema(description = "文章摘要", example = "本文介绍Spring Boot的基本使用方法...")
    private String summary;

    @Schema(description = "所属分类ID", example = "10", required = true)
    private Long categoryId;

    @TableField(exist = false)
    @Schema(description = "分类名称", example = "技术文章")
    private String categoryName;

    @Schema(description = "缩略图URL", example = "/images/2024/01/thumb.jpg")
    private String thumbnail;

    @Schema(description = "是否置顶", allowableValues = {"0", "1"}, example = "0")
    private String isTop;

    @Schema(description = "发布状态", allowableValues = {"0", "1"}, example = "1")
    private String status;

    @Schema(description = "访问量", example = "1280")
    private Long viewCount;

    @Schema(description = "是否允许评论", allowableValues = {"0", "1"}, example = "1")
    private String isComment;

    @Schema(description = "创建人ID", hidden = true)
    @TableField(fill = FieldFill.INSERT)
    private Long createBy;

    @Schema(description = "创建时间", example = "2024-01-15 10:30:00", hidden = true)
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @Schema(description = "修改人ID", hidden = true)
    @TableField(fill = FieldFill.UPDATE)
    private Long updateBy;

    @Schema(description = "修改时间", hidden = true)
    @TableField(fill = FieldFill.UPDATE)
    private Date updateTime;

    @Schema(description = "逻辑删除标记", allowableValues = {"0", "1"}, example = "0", hidden = true)
    private Integer delFlag;


}
