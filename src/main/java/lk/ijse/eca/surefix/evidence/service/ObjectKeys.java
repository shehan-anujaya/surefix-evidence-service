package lk.ijse.eca.surefix.evidence.service;

import java.util.UUID;
import java.util.regex.Pattern;

/** Object naming inside the bucket: runs/{runId}/{uuid}{.ext}. */
public final class ObjectKeys {

    private static final Pattern SAFE_ID = Pattern.compile("[A-Za-z0-9_-]{1,64}");
    private static final Pattern SAFE_NAME = Pattern.compile("[A-Za-z0-9_.-]{1,128}");

    private ObjectKeys() {}

    public static String prefix(String runId) {
        require(SAFE_ID, runId, "runId");
        return "runs/" + runId + "/";
    }

    public static String key(String runId, String filename) {
        require(SAFE_NAME, filename, "filename");
        return prefix(runId) + filename;
    }

    /** New unique object name that keeps the original extension (lower-cased, letters/digits only). */
    public static String newFilename(String originalFilename) {
        String ext = "";
        if (originalFilename != null) {
            int dot = originalFilename.lastIndexOf('.');
            if (dot >= 0 && dot < originalFilename.length() - 1) {
                String candidate = originalFilename.substring(dot + 1).toLowerCase();
                if (candidate.matches("[a-z0-9]{1,8}")) ext = "." + candidate;
            }
        }
        return UUID.randomUUID() + ext;
    }

    /** {runId, filename} from an object key, or null when the key is not in the expected layout. */
    public static String[] parse(String key) {
        String[] parts = key.split("/", 3);
        return parts.length == 3 && "runs".equals(parts[0]) && !parts[2].isEmpty() ? new String[] {parts[1], parts[2]} : null;
    }

    private static void require(Pattern p, String value, String what) {
        if (value == null || !p.matcher(value).matches()) {
            throw new IllegalArgumentException("Invalid " + what);
        }
    }
}
