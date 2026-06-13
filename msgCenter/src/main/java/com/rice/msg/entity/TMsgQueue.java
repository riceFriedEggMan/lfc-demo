package com.rice.msg.entity;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.baomidou.mybatisplus.annotation.TableName;
/**
 * 消息队列表(TMsgQueueHigh)表实体类
 *
 * @author makejava
 * @since 2026-06-13 09:57:53
 */
@SuppressWarnings("serial")
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("t_msg_queue_high")
public class TMsgQueue {
//ID@TableId
    private Long id;

//消息ID
    private String msgId;
//发给哪个用户
    private String to;
//消息主题
    private String subject;
//优先级
    private Integer priority;
//推送渠道，1：邮件，2:短信
    private Integer channel;
//模板ID
    private String templateId;
//模板传入参数
    private String templateData;
//状态
    private Integer status;
//创建时间
    private Date createTime;
//修改时间
    private Date modifyTime;



}
