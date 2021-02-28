package model;

import java.util.List;

public class EnvelopeResponse<T> {
    private T data;
    private List<Error> errors;

    private EnvelopeResponse() {}

    public static class Builder<T> {
        private T data;
        private List<Error> errors;

        public Builder() {}

        public Builder<T> withData(T data) {
            this.data = data;
            return this;
        }

        public Builder<T> withError(List<Error> errors) {
            this.errors = errors;
            return this;
        }

        public EnvelopeResponse<T> build() {
            EnvelopeResponse envelopeResponse = new EnvelopeResponse();
            envelopeResponse.data = this.data;
            envelopeResponse.errors = this.errors;
            return envelopeResponse;
        }
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public List<Error> getErrors() {
        return errors;
    }

    public void setErrors(List<Error> errors) {
        this.errors = errors;
    }
}
