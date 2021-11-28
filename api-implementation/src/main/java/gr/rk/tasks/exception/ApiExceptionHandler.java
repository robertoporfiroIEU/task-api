package gr.rk.tasks.exception;

import gr.rk.tasks.V1.dto.ErrorDTO;
import gr.rk.tasks.exception.i18n.I18nErrorMessage;
import gr.rk.tasks.util.Util;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.validation.ConstraintViolationException;
import java.time.LocalDateTime;

@ControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(value = {ApplicationException.class})
    public ResponseEntity<ErrorDTO> handleApplicationException(ApplicationException applicationException) {
       HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
        ErrorDTO errorDTO = new ErrorDTO()
                .status(ErrorDTO.StatusEnum.FAIL)
                .errorCode(applicationException.getI18nErrorMessage().getErrorCode())
                .httpStatus("" + httpStatus.value())
                .translateKey(applicationException.getI18nErrorMessage().getTranslateKey())
                .message(applicationException.getI18nErrorMessage().getSummary())
                .timestamp(Util.toDateISO8601WithTimeZone(LocalDateTime.now()));
        return new ResponseEntity<>(errorDTO, httpStatus);
    }

    @ExceptionHandler(value = {ConstraintViolationException.class})
    public ResponseEntity<ErrorDTO> handleConstraintViolationException(ConstraintViolationException constraintViolationException) {
        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
        ErrorDTO errorDTO = new ErrorDTO()
                .status(ErrorDTO.StatusEnum.FAIL)
                .errorCode(I18nErrorMessage.CONSTRAINT_VIOLATION.getErrorCode())
                .httpStatus("" + httpStatus.value())
                .translateKey(I18nErrorMessage.CONSTRAINT_VIOLATION.getTranslateKey())
                .message(constraintViolationException.getMessage())
                .timestamp(Util.toDateISO8601WithTimeZone(LocalDateTime.now()));
        return new ResponseEntity<>(errorDTO, httpStatus);
    }

    @ExceptionHandler(value = {RuntimeException.class})
    public ResponseEntity<ErrorDTO> handleSystemErrors(RuntimeException runtimeException) {
        HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        ErrorDTO errorDTO = new ErrorDTO()
                .status(ErrorDTO.StatusEnum.ERROR)
                .errorCode(I18nErrorMessage.SERVER_ERROR.getErrorCode())
                .httpStatus("" + httpStatus.value())
                .translateKey(I18nErrorMessage.SERVER_ERROR.getTranslateKey())
                .message(runtimeException.getMessage())
                .timestamp(Util.toDateISO8601WithTimeZone(LocalDateTime.now()));
        return new ResponseEntity<>(errorDTO, httpStatus);
    }
}
