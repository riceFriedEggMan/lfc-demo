package com.rice.lfcdemo.entity.vo;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserVo {

    private Long userId;

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


    private Date createTime;

    private Date updateTime;
}
