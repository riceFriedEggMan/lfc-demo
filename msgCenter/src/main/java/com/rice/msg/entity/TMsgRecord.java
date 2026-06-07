package com.rice.msg.entity;

import java.util.Date;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
/**
 * 消息记录表(TMsgRecord)表实体类
 *
 * @author makejava
 * @since 2026-06-07 12:24:40
 */
@SuppressWarnings("serial")
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("t_msg_record")
public class TMsgRecord  {

    private Long id;

    //消息ID
    private String msgId;
    //业务Id
    private String sourceId;
    //推送渠道
    private Integer channel;
    //消息主题
    private String subject;
    //发给哪个用户
    private String to;
    //模板ID
    private String templateId;
    //模板传入参数
    private String templateData;
    //状态, 1: 等待中, 2: 成功, 3: 失败
    private Integer status;
    //重试次数
    private Integer retryCount;
    //创建时间
    private Date createTime;
    //修改时间
    private Date modifyTime;



}
