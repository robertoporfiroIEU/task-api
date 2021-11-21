package gr.rk.tasks.exception.i18n;

import gr.rk.tasks.exception.ApplicationException;

public class AssignNotFoundException extends ApplicationException {

    public AssignNotFoundException(I18nErrorMessage i18nErrorMessageType) {
        super(i18nErrorMessageType);
    }
}
