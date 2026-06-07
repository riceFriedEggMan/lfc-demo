package com.rice.msg.enums;

public enum TemplateStatus {
    TEMPLATE_STATUS_PENDING(1),
    TEMPLATE_STATUS_NORMAL(2);

    private int status;

    private TemplateStatus(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }
}
