package domain.exception;

public class SaldoInsulficienteException extends RuntimeException {
    public SaldoInsulficienteException(String message) {
        super(message);
    }
}
