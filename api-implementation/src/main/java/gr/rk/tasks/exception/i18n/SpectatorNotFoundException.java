package gr.rk.tasks.exception.i18n;

import gr.rk.tasks.exception.ApplicationException;

public class SpectatorNotFoundException extends ApplicationException {

    public SpectatorNotFoundException(I18nErrorMessage i18nErrorMessageType) {
        super(i18nErrorMessageType);
    }
}
