package com.rice.lfcdemo.entity.dto;

import com.rice.lfcdemo.entity.param.NotifyHTTPParam;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TimerDTO {
    private Long timerId;

    /**
     * APP名称（所属业务）
     */
    private String app;

    /**
     * 定时任务-名称
     */
    private String name;

    /**
     * 定时任务-状态
     */
    private int status;

    /**
     *  定时任务-定时配置
     */
    private String cron;

    private NotifyHTTPParam notifyHTTPParam;
}
