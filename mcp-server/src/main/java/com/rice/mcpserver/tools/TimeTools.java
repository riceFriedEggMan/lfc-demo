package com.rice.mcpserver.tools;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.ZoneId;

@Component
public class TimeTools {

    @Tool(description = "通过时区获取当前时间")
    public String getTimeByZoneId(@ToolParam(description = "时区id，比如Asia/Shanghai") String zoneId){
        ZoneId zoneId1 = ZoneId.of(zoneId);
        ZonedDateTime now = ZonedDateTime.now(zoneId1);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z");
        return now.format(formatter);
    }
}