package lk.ijse.eca.surefix.evidence.service.impl;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;

import lk.ijse.eca.surefix.evidence.dto.EvidenceFile;
import lk.ijse.eca.surefix.evidence.service.StorageService;

/** Stores evidence files (screenshots, traces, logs) in a Google Cloud Storage bucket under runs/{runId}/. */
@Service
public class GcsStorageService implements StorageService {

    private final Storage storage = StorageOptions.getDefaultInstance().getService(); // Application Default Credentials
    private final String bucket;

    public GcsStorageService(@Value("${surefix.storage.bucket}") String bucket) {
        this.bucket = bucket;
    }

    @Override
    public EvidenceFile upload(String runId, MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }
        String original = file.getOriginalFilename();
        String ext = original != null && original.contains(".") ? original.substring(original.lastIndexOf('.')) : "";
        String filename = UUID.randomUUID() + ext;
        String contentType = file.getContentType() != null ? file.getContentType() : "application/octet-stream";
        try {
            Blob blob = storage.create(
                    BlobInfo.newBuilder(bucket, key(runId, filename)).setContentType(contentType).build(),
                    file.getBytes());
            return toDto(blob);
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file", e);
        }
    }

    @Override
    public List<EvidenceFile> list(String runId) {
        return storage.list(bucket, Storage.BlobListOption.prefix("runs/" + runId + "/"))
                .streamAll()
                .map(this::toDto)
                .toList();
    }

    @Override
    public StoredObject load(String runId, String filename) {
        Blob blob = storage.get(BlobId.of(bucket, key(runId, filename)));
        if (blob == null) {
            throw new IllegalArgumentException("File not found: " + filename);
        }
        return new StoredObject(blob.getContent(), blob.getContentType());
    }

    @Override
    public void delete(String runId, String filename) {
        if (!storage.delete(BlobId.of(bucket, key(runId, filename)))) {
            throw new IllegalArgumentException("File not found: " + filename);
        }
    }

    private static String key(String runId, String filename) {
        return "runs/" + runId + "/" + filename;
    }

    private EvidenceFile toDto(Blob blob) {
        String[] parts = blob.getName().split("/", 3); // runs / {runId} / {filename}
        return new EvidenceFile(parts[1], parts[2], blob.getContentType(), blob.getSize(),
                "/api/v1/evidence/" + parts[1] + "/" + parts[2]);
    }
}
