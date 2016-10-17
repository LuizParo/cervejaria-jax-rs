package br.com.geladaonline.exception;

public class CervejaJaExisteException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public CervejaJaExisteException(String message, Throwable cause) {
        super(message, cause);
    }

    public CervejaJaExisteException(String message) {
        super(message);
    }

    public CervejaJaExisteException(Throwable cause) {
        super(cause);
    }
}