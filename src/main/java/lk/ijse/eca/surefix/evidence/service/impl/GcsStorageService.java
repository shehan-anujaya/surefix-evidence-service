package lk.ijse.eca.surefix.evidence.service.impl;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;

import lk.ijse.eca.surefix.evidence.dto.EvidenceFile;
import lk.ijse.eca.surefix.evidence.exception.EvidenceNotFoundException;
import lk.ijse.eca.surefix.evidence.service.ObjectKeys;
import lk.ijse.eca.surefix.evidence.service.StorageService;

/** Stores evidence files (screenshots, traces, logs) in a Google Cloud Storage bucket under runs/{runId}/. */
@Service
public class GcsStorageService implements StorageService {

    private final Storage storage;
    private final String bucket;

    public GcsStorageService(@Value("${surefix.storage.bucket}") String bucket) {
        this(StorageOptions.getDefaultInstance().getService(), bucket); // Application Default Credentials
    }

    GcsStorageService(Storage storage, String bucket) {
        this.storage = storage;
        this.bucket = bucket;
    }

    @Override
    public EvidenceFile upload(String runId, MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }
        String filename = ObjectKeys.newFilename(file.getOriginalFilename());
        String contentType = file.getContentType() != null ? file.getContentType() : "application/octet-stream";
        try {
            Blob blob = storage.create(
                    BlobInfo.newBuilder(bucket, ObjectKeys.key(runId, filename))
                            .setContentType(contentType)
                            .setMetadata(java.util.Map.of("originalFilename",
                                    Objects.requireNonNullElse(file.getOriginalFilename(), filename)))
                            .build(),
                    file.getBytes());
            return toDto(blob);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to read the uploaded file", e);
        }
    }

    @Override
    public List<EvidenceFile> list(String runId) {
        return storage.list(bucket, Storage.BlobListOption.prefix(ObjectKeys.prefix(runId)))
                .streamAll()
                .map(this::toDto)
                .filter(Objects::nonNull)
                .sorted((a, b) -> b.uploadedAt().compareTo(a.uploadedAt()))
                .toList();
    }

    @Override
    public StoredObject load(String runId, String filename) {
        Blob blob = storage.get(BlobId.of(bucket, ObjectKeys.key(runId, filename)));
        if (blob == null) {
            throw new EvidenceNotFoundException(runId, filename);
        }
        return new StoredObject(blob.getContent(), blob.getContentType());
    }

    @Override
    public void delete(String runId, String filename) {
        if (!storage.delete(BlobId.of(bucket, ObjectKeys.key(runId, filename)))) {
            throw new EvidenceNotFoundException(runId, filename);
        }
    }

    private EvidenceFile toDto(Blob blob) {
        String[] parts = ObjectKeys.parse(blob.getName());
        if (parts == null) return null;
        Instant uploadedAt = blob.getCreateTimeOffsetDateTime() != null ? blob.getCreateTimeOffsetDateTime().toInstant() : Instant.EPOCH;
        return new EvidenceFile(parts[0], parts[1], blob.getContentType(), blob.getSize() == null ? 0 : blob.getSize(),
                uploadedAt, "/api/v1/evidence/" + parts[0] + "/" + parts[1]);
    }
}
