package com.rice.lfcdemo.controller;

import com.rice.lfcdemo.domain.ResponseResult;
import com.rice.lfcdemo.service.LinkService;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/link")
public class LinkController {
    @Autowired
    private LinkService linkService;

    @GetMapping("/getAllLinks")
    public ResponseResult getAllLinks() {
        return linkService.getAllLinks();
    }
}
