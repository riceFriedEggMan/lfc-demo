package com.rice.lfcdemo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.rice.lfcdemo.domain.ResponseResult;
import com.rice.lfcdemo.entity.Tag;
import com.rice.lfcdemo.entity.dto.TagDto;


/**
 * 标签(Tag)表服务接口
 *
 * @author makejava
 * @since 2026-05-28 15:30:54
 */
public interface TagService extends IService<Tag> {

    ResponseResult tagPage(Integer pageNum, Integer pageSize, TagDto tagDto);

    ResponseResult listAll();

}
