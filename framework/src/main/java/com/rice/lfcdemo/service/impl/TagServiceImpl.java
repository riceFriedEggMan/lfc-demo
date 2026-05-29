package com.rice.lfcdemo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rice.lfcdemo.domain.ResponseResult;
import com.rice.lfcdemo.entity.Tag;
import com.rice.lfcdemo.entity.dto.TagDto;
import com.rice.lfcdemo.entity.vo.PageVo;
import com.rice.lfcdemo.entity.vo.TagVo;
import com.rice.lfcdemo.mapper.TagMapper;
import com.rice.lfcdemo.service.TagService;
import com.rice.lfcdemo.utils.BeanCopyUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 标签(Tag)表服务实现类
 *
 * @author makejava
 * @since 2026-05-28 15:30:54
 */
@Service
public class TagServiceImpl extends ServiceImpl<TagMapper, Tag> implements TagService {

    @Override
    public ResponseResult tagPage(Integer pageNum, Integer pageSize, TagDto tagDto) {
        Tag tag = BeanCopyUtils.copy(tagDto, Tag.class);
        LambdaQueryWrapper<Tag> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StringUtils.hasText(tag.getName()), Tag::getName, tag.getName());
        wrapper.eq(StringUtils.hasText(tag.getRemark()), Tag::getRemark, tag.getRemark());
        Page<Tag> page = new Page<>(pageNum, pageSize);
        page(page, wrapper);

        List<Tag> records = page.getRecords();
        PageVo pageVo = new PageVo(records, page.getTotal());
        return ResponseResult.ok(pageVo);
    }

    @Override
    public ResponseResult listAll() {
        LambdaQueryWrapper<Tag> wrapper = new LambdaQueryWrapper<>();
        wrapper.select(Tag::getId, Tag::getName);
        List<Tag> tags = this.list(wrapper);
        List<TagVo> tagVos = BeanCopyUtils.copyList(tags, TagVo.class);
        return ResponseResult.ok(tagVos);
    }
}
