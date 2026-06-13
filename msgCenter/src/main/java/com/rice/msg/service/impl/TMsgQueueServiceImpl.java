package com.rice.msg.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rice.msg.entity.TMsgQueue;
import com.rice.msg.mapper.TMsgQueueMapper;
import com.rice.msg.service.TMsgQueueService;
import org.springframework.stereotype.Service;

/**
 * 消息队列表(TMsgQueueHigh)表服务实现类
 *
 * @author makejava
 * @since 2026-06-13 09:57:53
 */
@Service()
public class TMsgQueueServiceImpl extends ServiceImpl<TMsgQueueMapper, TMsgQueue> implements TMsgQueueService {

}
