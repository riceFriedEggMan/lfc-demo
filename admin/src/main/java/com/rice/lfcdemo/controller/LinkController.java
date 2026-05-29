package com.rice.lfcdemo.controller;

import com.rice.lfcdemo.domain.ResponseResult;
import com.rice.lfcdemo.entity.Link;
import com.rice.lfcdemo.service.LinkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/link")
public class LinkController {
    @Autowired
    private LinkService linkService;

    @PostMapping("/add")
    public ResponseResult add(Link link) {
         linkService.save(link);
         return ResponseResult.ok();
    }

    @DeleteMapping("/delete")
    public ResponseResult delete(@RequestParam(value = "ids") String ids) {
        if (!ids.contains(",")){
            linkService.removeById(ids);
        }else{
            String[] idList = ids.split(",");
            for (String id : idList){
                linkService.removeById(id);
            }

        }
        return  ResponseResult.ok();
    }

    @PutMapping("/edit")
    public ResponseResult edit(@RequestBody  Link link) {
        linkService.updateById(link);
        return ResponseResult.ok();
    }

    @GetMapping("/{id}")
    public ResponseResult getInfo(@PathVariable(value = "id") Long id) {
        Link link = linkService.getById(id);
        return ResponseResult.ok(link);
    }

    @GetMapping("/page")
    public ResponseResult pageLink(Link link, Integer pageNo, Integer pageSize) {
        return linkService.pageLink(link, pageNo, pageSize);
    }


}
