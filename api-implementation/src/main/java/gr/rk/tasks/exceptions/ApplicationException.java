package gr.rk.tasks.exceptions;

import gr.rk.tasks.exceptions.i18n.I18nErrorMessage;

public class ApplicationException extends RuntimeException {

    private final I18nErrorMessage i18nErrorMessage;

    public ApplicationException(I18nErrorMessage i18nErrorMessage) {
        super(i18nErrorMessage.toString());
        this.i18nErrorMessage = i18nErrorMessage;
    }

    public I18nErrorMessage getI18nErrorMessage() {
        return i18nErrorMessage;
    }
}
