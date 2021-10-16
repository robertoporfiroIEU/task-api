package gr.rk.tasks.exceptions.i18n;

import gr.rk.tasks.exceptions.ApplicationException;

public class ProjectNotFoundException extends ApplicationException {

    public ProjectNotFoundException(I18nErrorMessage i18nErrorMessageType) {
        super(i18nErrorMessageType);
    }
}
