package com.rice.msg.tools;

public interface RateLimitService {
    boolean isRequestAllowed(String sourceId, Integer channel, boolean isTimerMsg);
}
