package lk.ijse.eca.surefix.evidence.exception;

public class EvidenceNotFoundException extends RuntimeException {
    public EvidenceNotFoundException(String runId, String filename) {
        super("Evidence not found: runs/" + runId + "/" + filename);
    }
}
