package com.rice.lfcdemo.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.rice.lfcdemo.domain.ResponseResult;
import com.rice.lfcdemo.entity.Tag;
import com.rice.lfcdemo.entity.dto.TagDto;
import com.rice.lfcdemo.entity.vo.PageVo;
import com.rice.lfcdemo.service.TagService;
import com.rice.lfcdemo.utils.BeanCopyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("tag")
public class TagController {
    @Autowired
    private TagService tagService;

    @PostMapping("/add")
    public ResponseResult add(@RequestBody TagDto tagDto) {
        Tag tag = BeanCopyUtils.copy(tagDto, Tag.class);
        tagService.save(tag);
        return ResponseResult.ok();
    }

    @GetMapping("/{id}")
    public ResponseResult get(@PathVariable String id) {
        return ResponseResult.ok(tagService.getById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseResult delete(@PathVariable String id) {
        LambdaQueryWrapper<Tag> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Tag::getId, id);
        tagService.remove(wrapper);
        return ResponseResult.ok();
    }

    @PutMapping("/update")
    public ResponseResult update(@RequestBody TagDto tagDto) {
        tagService.updateById(BeanCopyUtils.copy(tagDto, Tag.class));
        return ResponseResult.ok();
    }

    @GetMapping("list")
    public ResponseResult list(Integer pageNum, Integer pageSize, TagDto tagDto) {
        return tagService.tagPage(pageNum, pageSize, tagDto);
    }

    @GetMapping("/listAll")
    public ResponseResult listAll(){
        return tagService.listAll();
    }


}
