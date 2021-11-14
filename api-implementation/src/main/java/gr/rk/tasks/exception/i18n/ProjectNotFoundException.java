package gr.rk.tasks.exception.i18n;

import gr.rk.tasks.exception.ApplicationException;

public class ProjectNotFoundException extends ApplicationException {

    public ProjectNotFoundException(I18nErrorMessage i18nErrorMessageType) {
        super(i18nErrorMessageType);
    }
}