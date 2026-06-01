package com.rice.lfcdemo.entity.param;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotifyHTTPParam {
    private String method;
    private String url;
    private Map<String,String> header;
    private String body;
}
