package yasmine.Errors;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

public class ErrorHandler {
    @ResponseStatus(value=HttpStatus.BAD_REQUEST, reason="Wrong date format")  // 400
    public class IncorrectDate extends RuntimeException {
        // ...
    }
}
