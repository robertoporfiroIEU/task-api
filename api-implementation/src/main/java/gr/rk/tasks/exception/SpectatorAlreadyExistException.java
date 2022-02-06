package gr.rk.tasks.exception;

import gr.rk.tasks.exception.i18n.I18nErrorMessage;

public class SpectatorAlreadyExistException extends ApplicationException {

    public SpectatorAlreadyExistException(I18nErrorMessage i18nErrorMessageType) {
        super(i18nErrorMessageType);
    }
}
