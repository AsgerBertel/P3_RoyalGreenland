package directory.update;

/**
 * Exception used for files not a part of the DMS system.
 * Specificially for files in which their path does not include DMSApplication.APP_TITLE
 */

public class IllegalFileException extends Exception
{
    public IllegalFileException (String message) {
        super(message);
    }

    public IllegalFileException (String message, Throwable cause) {
        super(message, cause);
    }

    public IllegalFileException (Throwable cause) {
        super(cause);
    }

    public IllegalFileException (String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
