package lk.ijse.eca.surefix.evidence.exception;

import java.time.Instant;
import java.util.Map;

/** Uniform error body returned by every SureFix service. */
public record ApiError(Instant timestamp, int status, String error, String message, String path,
                       Map<String, String> fieldErrors) {
}
