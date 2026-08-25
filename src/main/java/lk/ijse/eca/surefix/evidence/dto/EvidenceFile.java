package lk.ijse.eca.surefix.evidence.dto;

import java.time.Instant;

public record EvidenceFile(String runId, String filename, String contentType, long size, Instant uploadedAt, String url) {
}
