package com.rice.lfcdemo.entity;

import java.util.Date;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
/**
 * 分类表(Category)表实体类
 *
 * @author makejava
 * @since 2026-05-23 19:40:54
 */
@SuppressWarnings("serial")
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("category")
public class Category  {
@TableId
    private Long id;

    //分类名
    private String name;
    //父类id
    private Long pid;
    //描述
    private String description;
    //0,正常；1禁用
    private String status;

    private Long createBy;

    private Date createTime;

    private Long updateBy;

    private Date updateTime;
    //0未删除，1已删除
    private Integer delFlag;



}
