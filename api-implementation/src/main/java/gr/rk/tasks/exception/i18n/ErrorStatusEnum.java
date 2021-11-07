package gr.rk.tasks.exception.i18n;

public enum ErrorStatusEnum {

    /**
     * There was a problem with the data submitted,
     * or some pre-condition of the API call wasn't satisfied
     */
    FAIL("fail"),
    /**
     * An error occurred in processing the request,
     * i.e. an exception was thrown
     */
    ERROR("error");

    private String value;

    ErrorStatusEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static ErrorStatusEnum fromValue(String value) {

        for (ErrorStatusEnum errorStatusEnum : ErrorStatusEnum.values()) {

            if (errorStatusEnum.value.equals(value)) {
                return errorStatusEnum;
            }
        }

        return null;
    }

}
