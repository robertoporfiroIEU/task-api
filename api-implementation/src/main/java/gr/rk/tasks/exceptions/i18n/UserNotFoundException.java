package gr.rk.tasks.exceptions.i18n;

import gr.rk.tasks.exceptions.ApplicationException;

public class UserNotFoundException extends ApplicationException {

    public UserNotFoundException(I18nErrorMessage i18nErrorMessageType) {
        super(i18nErrorMessageType);
    }
}
