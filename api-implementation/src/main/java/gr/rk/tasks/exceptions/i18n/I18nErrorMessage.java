package gr.rk.tasks.exceptions.i18n;

import java.util.HashSet;
import java.util.Set;

public enum I18nErrorMessage {

    TASK_NOT_FOUND("The task not found", "001", 400, "taskApi.task-not-found", ErrorStatusEnum.FAIL),
    USER_NOT_FOUND("The user not found", "002", 400, "taskApi.user-not-found", ErrorStatusEnum.FAIL);

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
