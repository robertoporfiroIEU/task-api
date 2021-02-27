package model;


public class Error {
    public enum Status {
        fail,
        error
    };
    private Status status;
    private String message;
    private String description;

    public Error(Status status, String message, String description) {
        this.status = status;
        this.message = message;
        this.description = description;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
