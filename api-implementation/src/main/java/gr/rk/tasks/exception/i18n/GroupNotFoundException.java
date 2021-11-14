package gr.rk.tasks.exception.i18n;

import gr.rk.tasks.exception.ApplicationException;

public class GroupNotFoundException extends ApplicationException {

    public GroupNotFoundException(I18nErrorMessage i18nErrorMessageType) {
        super(i18nErrorMessageType);
    }
}
