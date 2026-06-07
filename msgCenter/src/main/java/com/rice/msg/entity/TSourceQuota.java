package com.rice.msg.entity;

import java.util.Date;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
/**
 * 业务限额表(TSourceQuota)表实体类
 *
 * @author makejava
 * @since 2026-06-07 12:29:44
 */
@SuppressWarnings("serial")
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("t_source_quota")
public class TSourceQuota  {
    //ID@TableId
    private Long id;

    //业务ID
    private String sourceId;
    //限额
    private Integer num;
    //限频单位，单位毫秒
    private Integer unit;
    //推送渠道，1：邮件，2:短信
    private Integer channel;
    //创建时间
    private Date createTime;
    //修改时间
    private Date modifyTime;



}
