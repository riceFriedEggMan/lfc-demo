package com.rice.msg.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rice.msg.entity.TMsgRecord;
import com.rice.msg.mapper.TMsgRecordMapper;
import com.rice.msg.service.TMsgRecordService;
import org.springframework.stereotype.Service;

/**
 * 消息记录表(TMsgRecord)表服务实现类
 *
 * @author makejava
 * @since 2026-06-07 12:24:42
 */
@Service
public class TMsgRecordServiceImpl extends ServiceImpl<TMsgRecordMapper, TMsgRecord> implements TMsgRecordService {

}
