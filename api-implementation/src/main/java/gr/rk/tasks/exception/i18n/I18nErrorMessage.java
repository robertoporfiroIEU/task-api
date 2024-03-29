package gr.rk.tasks.exception.i18n;

import org.springframework.beans.factory.annotation.Value;

import java.util.HashSet;
import java.util.Set;

public enum I18nErrorMessage {

    TASK_NOT_FOUND("Task not found", "001", 400, "taskApi.task-not-found", ErrorStatusEnum.FAIL),
    USER_NOT_FOUND("User not found", "002", 400, "taskApi.user-not-found", ErrorStatusEnum.FAIL),
    PROJECT_NOT_FOUND("Project not found", "003", 400, "taskApi.project-not-found", ErrorStatusEnum.FAIL),
    USER_AND_GROUP_NOT_FOUND("User and Group not found", "004", 400, "taskApi.user-and-group-not-found", ErrorStatusEnum.FAIL),
    GROUP_NOT_FOUND("Group not found", "005", 400, "taskApi.group-not-found", ErrorStatusEnum.FAIL),
    COMMENT_NOT_FOUND("Comment not found", "006", 400, "taskApi.comment-not-found", ErrorStatusEnum.FAIL),
    ASSIGN_NOT_FOUND("Assign not found", "007", 400, "taskApi.assign-not-found", ErrorStatusEnum.FAIL),
    ASSIGN_ALREADY_EXIST("Assign already exist", "008", 400, "taskApi.assign-already-exist", ErrorStatusEnum.FAIL),
    SPECTATOR_NOT_FOUND("Spectator not found", "009", 400, "taskApi.spectator-not-found", ErrorStatusEnum.FAIL),
    SPECTATOR_ALREADY_EXIST("Spectator already exist", "010", 400, "taskApi.spectator-already-exist", ErrorStatusEnum.FAIL),
    INVALID_SORT_FIELD("Invalid sort field", "011", 400, "taskApi.invalid-sort-field", ErrorStatusEnum.FAIL),
    PROPAGATED_USER_IS_NOT_SAME("Propagated user is not the same as the caller of the API", "012", 400, "taskApi.propagated-user-is-not-same", ErrorStatusEnum.FAIL),
    CONSTRAINT_VIOLATION("Constraint Violation", "400", 400, "taskApi.constraint-violation", ErrorStatusEnum.FAIL),
    SERVER_ERROR("Server Error", "500", 500, "taskApi.server-error", ErrorStatusEnum.ERROR),
    ATTACHMENT_NOT_FOUND("Attachment not found", "013", 400, "taskApi.attachment-not-found", ErrorStatusEnum.FAIL);

    private String summary;
    private String errorCode;
    private int httpStatus;
    private String translateKey;
    private ErrorStatusEnum errorStatus;

    private I18nErrorMessage(String summary, String errorCode, int httpStatus, String translateKey, ErrorStatusEnum errorStatus) {
        this.summary = summary;
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
        this.translateKey = translateKey;
        this.errorStatus = errorStatus;
    }

    public static Set<String> getEnums() {
        HashSet<String> values = new HashSet<>();
        for (I18nErrorMessage i18nErrorMessageType : I18nErrorMessage.values()) {
            values.add(i18nErrorMessageType.name());
        }
        return values;
    }

    public String getSummary() {
        return summary;
    }

    public String getTranslateKey() {
        return translateKey;
    }

    public int getHttpStatus() {
        return httpStatus;
    }

    public ErrorStatusEnum getErrorStatus() {
        return errorStatus;
    }

    public String getErrorCode() {
        return errorCode;
    }

    @Override
    public String toString() {
        return "I18nErrorMessage{" +
                "summary='" + summary + '\'' +
                ", errorCode='" + errorCode + '\'' +
                ", httpStatus=" + httpStatus +
                ", translateKey='" + translateKey + '\'' +
                ", errorStatus=" + errorStatus +
                '}';
    }
}
