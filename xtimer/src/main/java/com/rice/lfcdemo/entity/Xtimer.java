package com.rice.lfcdemo.entity;

import java.util.Date;

import java.io.Serializable;

import com.rice.lfcdemo.entity.dto.TimerDTO;
import com.rice.lfcdemo.entity.param.NotifyHTTPParam;
import com.rice.lfcdemo.model.BaseModel;
import com.rice.lfcdemo.utils.JSONUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import org.springframework.beans.BeanUtils;

/**
 * Timer 信息(Xtimer)表实体类
 *
 * @author makejava
 * @since 2026-06-01 15:08:19
 */
@SuppressWarnings("serial")
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("xtimer")
public class Xtimer extends BaseModel {

    private Long timerId;

    //创建时间
    private Date createTime;
    //更新时间
    private Date modifyTime;
    //app
    private String app;
    //name
    private String name;
    //0新建，1激活，2未激活
    private Integer status;
    //cron表达式
    private String cron;
    //回调上下文
    private String notifyHttpParam;

    public static Xtimer voToObj(TimerDTO timerDTO) {
        if (timerDTO == null) {
            return null;
        }
        Xtimer xtimer = new Xtimer();
        xtimer.setApp(timerDTO.getApp());
        xtimer.setName(timerDTO.getName());
        xtimer.setStatus(timerDTO.getStatus());
        xtimer.setCron(timerDTO.getCron());
        xtimer.setTimerId(timerDTO.getTimerId());
        xtimer.setNotifyHttpParam(JSONUtil.toJsonString(timerDTO.getNotifyHTTPParam()));
        return xtimer;
    }

    public static TimerDTO objToVo(Xtimer xtimer) {
        if (xtimer == null) {
            return null;
        }
        TimerDTO timerDTO = new TimerDTO();
        timerDTO.setApp(xtimer.getApp());
        timerDTO.setName(xtimer.getName());
        timerDTO.setStatus(xtimer.getStatus());
        timerDTO.setTimerId(xtimer.getTimerId());
        timerDTO.setCron(xtimer.getCron());

        NotifyHTTPParam notifyHTTPParam = JSONUtil.parseObject(xtimer.getNotifyHttpParam(), NotifyHTTPParam.class);
        timerDTO.setNotifyHTTPParam(notifyHTTPParam);
        BeanUtils.copyProperties(xtimer, timerDTO);
        return timerDTO;
    }





}
