package gr.rk.tasks.exceptions;

import gr.rk.tasks.exceptions.i18n.I18nErrorMessage;

public class TaskNotFoundException extends ApplicationException {

    public TaskNotFoundException(I18nErrorMessage i18nErrorMessageType) {
        super(i18nErrorMessageType);
    }
}
