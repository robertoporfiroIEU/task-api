package gr.rk.tasks.exceptions;

import gr.rk.tasks.exceptions.i18n.I18nErrorMessage;

public class ApplicationException extends RuntimeException {

    private I18nErrorMessage i18nErrorMessage;

    public ApplicationException(I18nErrorMessage i18nErrorMessage) {
        super(i18nErrorMessage.toString());
        this.i18nErrorMessage = i18nErrorMessage;
    }

    public I18nErrorMessage getI18nErrorMessage() {
        return i18nErrorMessage;
    }

    public void setI18nErrorMessage(I18nErrorMessage i18nErrorMessage) {
        this.i18nErrorMessage = i18nErrorMessage;
    }
}
