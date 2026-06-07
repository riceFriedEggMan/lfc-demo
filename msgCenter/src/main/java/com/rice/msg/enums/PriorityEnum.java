package com.rice.msg.enums;

public enum PriorityEnum {
    PRIORITY_LOW(1),
    PRIORITY_MIDDLE(2),
    PRIORITY_HIGH(3),
    PRIORITY_RETRY(4);

    private int priority;

    private PriorityEnum(int priority) {
        this.priority = priority;
    }

    public int getPriority() {
        return priority;
    }

    public static String GetPriorityStr(int priority) {
        return switch (priority) {
            case 2 -> "middle";
            case 3 -> "high";
            case 4 -> "retry";
            default -> "low";
        };
    }
}
