package com.rice.lfcdemo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.rice.lfcdemo.domain.ResponseResult;
import com.rice.lfcdemo.entity.Link;


/**
 * 友链(Link)表服务接口
 *
 * @author makejava
 * @since 2026-05-28 13:47:03
 */
public interface LinkService extends IService<Link> {

    ResponseResult getAllLinks();

    ResponseResult pageLink(Link link, Integer pageNo, Integer pageSize);

}
