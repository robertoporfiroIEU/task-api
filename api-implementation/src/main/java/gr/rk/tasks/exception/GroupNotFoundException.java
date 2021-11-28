package gr.rk.tasks.exception;

import gr.rk.tasks.exception.i18n.I18nErrorMessage;

public class GroupNotFoundException extends ApplicationException {

    public GroupNotFoundException(I18nErrorMessage i18nErrorMessageType) {
        super(i18nErrorMessageType);
    }
}
