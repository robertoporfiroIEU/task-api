package gr.rk.tasks.exception.i18n;

import gr.rk.tasks.exception.ApplicationException;

public class UserNotFoundException extends ApplicationException {

    public UserNotFoundException(I18nErrorMessage i18nErrorMessageType) {
        super(i18nErrorMessageType);
    }
}
