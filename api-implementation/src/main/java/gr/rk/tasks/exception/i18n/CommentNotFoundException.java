package gr.rk.tasks.exception.i18n;

import gr.rk.tasks.exception.ApplicationException;

public class CommentNotFoundException extends ApplicationException {

    public CommentNotFoundException(I18nErrorMessage i18nErrorMessageType) {
        super(i18nErrorMessageType);
    }
}
