package bsr.bank.exception;

/**
 * Created by marcin on 19.11.16.
 */
public class NoAccountException extends Exception {
    public NoAccountException() {
    }

    public NoAccountException(String message) {
        super(message);
    }
}
