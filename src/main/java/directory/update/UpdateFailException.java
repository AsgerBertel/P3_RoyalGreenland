package directory.update;

public class UpdateFailException extends Exception
{
    public UpdateFailException () {
        super();
    }

    public UpdateFailException (String message) {
        super(message);
    }

    public UpdateFailException (String message, Throwable cause) {
        super(message, cause);
    }

    public UpdateFailException (Throwable cause) {
        super(cause);
    }
    protected UpdateFailException (String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
