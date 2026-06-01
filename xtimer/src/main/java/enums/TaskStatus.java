package enums;

public enum TaskStatus {
    NotRun(0),
    Running(1),
    Succeed(2),
    Failed(3);


    private int status;

    private TaskStatus(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }
}
