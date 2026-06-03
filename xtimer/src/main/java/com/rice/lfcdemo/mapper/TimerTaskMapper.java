package com.rice.lfcdemo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.rice.lfcdemo.entity.TaskModel;
import org.apache.ibatis.annotations.Param;

import java.util.List;


/**
 * Timer Task任务信息(TimerTask)表数据库访问层
 *
 * @author makejava
 * @since 2026-06-02 15:15:18
 */
public interface TimerTaskMapper extends BaseMapper<TaskModel> {

    void batchSave(@Param("taskList") List<TaskModel> taskModels);
}
