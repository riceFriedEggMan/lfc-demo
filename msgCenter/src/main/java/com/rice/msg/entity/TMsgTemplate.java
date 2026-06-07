package com.rice.msg.entity;

import java.util.Date;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
/**
 * 消息模板表(TMsgTemplate)表实体类
 *
 * @author makejava
 * @since 2026-06-07 12:25:00
 */
@SuppressWarnings("serial")
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("t_msg_template")
public class TMsgTemplate  {

    private Long id;

    //模板ID
    private String templateId;
    //关联模板ID
    private String relTemplateId;
    //模板名字
    private String name;
    //签名
    private String signName;
    //业务ID
    private String sourceId;
    //推送渠道，1：邮件，2:短信
    private Integer channel;
    //消息主题
    private String subject;
    //消息文本模板
    private String content;
    //状态，未激活还是正常
    private Integer status;
    //创建时间
    private Date createTime;
    //修改时间
    private Date modifyTime;



}
