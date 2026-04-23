package tech.derfeb.portfolio_cms.Exception;

public class InvalidInputException extends Exception {

    public enum InputTypes {
        Email
    }

    public InvalidInputException(InputTypes type, String message) {
        super(type.toString() + " " + message);
    }
}
