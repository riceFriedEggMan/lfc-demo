package com.rice.lfcdemo.enums;

public enum TimerStatus {
    Enable(1),
    Unable(0);


    private int status;

    private TimerStatus(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }

    public static TimerStatus getStatus(int status) {
        for (TimerStatus timerStatus : TimerStatus.values()) {
            if (timerStatus.getStatus() == status) {
                return timerStatus;
            }
        }
        return null;
    }
}
