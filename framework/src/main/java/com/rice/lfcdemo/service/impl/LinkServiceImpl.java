package com.rice.lfcdemo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rice.lfcdemo.constants.SystemConstants;
import com.rice.lfcdemo.domain.ResponseResult;
import com.rice.lfcdemo.entity.Link;
import com.rice.lfcdemo.entity.vo.LinkVo;
import com.rice.lfcdemo.entity.vo.PageVo;
import com.rice.lfcdemo.mapper.LinkMapper;
import com.rice.lfcdemo.service.LinkService;
import com.rice.lfcdemo.utils.BeanCopyUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 友链(Link)表服务实现类
 *
 * @author makejava
 * @since 2026-05-28 13:47:03
 */
@Service
public class LinkServiceImpl extends ServiceImpl<LinkMapper, Link> implements LinkService {

    @Override
    public ResponseResult getAllLinks() {
        LambdaQueryWrapper<Link> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Link::getStatus, SystemConstants.LINK_STATUS_NORMAL);
        List<Link> links = list(wrapper);
        List<LinkVo> linkVos = BeanCopyUtils.copyList(links, LinkVo.class);
        return ResponseResult.ok(linkVos);
    }

    @Override
    public ResponseResult pageLink(Link link, Integer pageNo, Integer pageSize) {
        LambdaQueryWrapper<Link> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StringUtils.hasText(link.getName()), Link::getName, link.getName());
        wrapper.eq(StringUtils.hasText(link.getStatus()), Link::getStatus, link.getStatus());

        Page<Link> page = new Page<>(pageNo, pageSize);
        page(page, wrapper);
        PageVo pageVo = new PageVo(page.getRecords(), page.getTotal());
        return ResponseResult.ok(pageVo);
    }
}
