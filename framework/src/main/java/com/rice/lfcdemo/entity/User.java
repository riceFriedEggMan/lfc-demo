package com.rice.lfcdemo.entity;

import java.util.Date;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
/**
 * 用户表(User)表实体类
 *
 * @author makejava
 * @since 2026-05-09 09:07:30
 */
@SuppressWarnings("serial")
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("user")
public class User  {
    //用户id@TableId
    @TableId
    private String userId;

    //用户名
    private String userName;

    private String nickName;
    //用户密码
    private String password;
    //手机号
    private String phone;
    //用户邮件
    private String email;

    private String sex;
    // 头像
    private String avatar;
    //用户状态
    private Integer status;
    //创建时间
    private Date createTime;
    //更新时间
    private Date updateTime;



}
