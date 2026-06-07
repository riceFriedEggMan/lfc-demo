package com.rice.msg.enums;

public enum MsgStatus {
    Pending(1),
    Processing(2),
    Success(3),
    Failed(4);


    private int status;

    private MsgStatus(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }
}
