package com.rice.lfcdemo.controller;

import com.rice.lfcdemo.entity.Xtimer;
import com.rice.lfcdemo.entity.dto.TimerDTO;
import com.rice.lfcdemo.model.ResponseEntity;
import com.rice.lfcdemo.service.XtimerService;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/Xtimer")
public class XtimerController {
    @Autowired
    private XtimerService xtimerService;

    @PostMapping("/addXtimer")
    public ResponseEntity addXtimer(@RequestBody TimerDTO timerDTO) {
        Long id = xtimerService.createTimer(timerDTO);
        return ResponseEntity.ok(id);
    }

    @GetMapping("/enableXtimer")
    public ResponseEntity enableXtimer(@RequestParam String app,
                                 @RequestParam Long timerId){
        return xtimerService.enableXtimer(app, timerId);

    }
}
