package gr.rk.tasks.exception;

import gr.rk.tasks.exception.i18n.I18nErrorMessage;

public class InvalidSortFieldException extends ApplicationException {

    public InvalidSortFieldException(I18nErrorMessage i18nErrorMessageType) {
        super(i18nErrorMessageType);
    }
}
