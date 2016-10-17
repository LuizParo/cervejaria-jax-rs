package br.com.geladaonline.exception;

public class CervejaNaoEncontradaException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public CervejaNaoEncontradaException(String message, Throwable cause) {
        super(message, cause);
    }

    public CervejaNaoEncontradaException(String message) {
        super(message);
    }

    public CervejaNaoEncontradaException(Throwable cause) {
        super(cause);
    }
}