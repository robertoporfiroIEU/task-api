package gr.rk.tasks.exception.i18n;

import gr.rk.tasks.exception.ApplicationException;

public class UserOrGroupNotFoundException extends ApplicationException {

    public UserOrGroupNotFoundException(I18nErrorMessage i18nErrorMessageType) {
        super(i18nErrorMessageType);
    }
}
