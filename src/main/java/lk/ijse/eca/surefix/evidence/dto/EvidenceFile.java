package lk.ijse.eca.surefix.evidence.dto;

import java.time.Instant;

/**
 * @param filename         the generated object name inside the bucket (used for load / delete)
 * @param originalFilename the name the file was uploaded with, kept as object metadata
 */
public record EvidenceFile(String runId, String filename, String originalFilename, String contentType,
                           long size, Instant uploadedAt, String url) {
}
