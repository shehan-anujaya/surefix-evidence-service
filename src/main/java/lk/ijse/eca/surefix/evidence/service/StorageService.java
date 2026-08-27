package lk.ijse.eca.surefix.evidence.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import lk.ijse.eca.surefix.evidence.dto.EvidenceFile;

public interface StorageService {
    EvidenceFile upload(String runId, MultipartFile file);
    List<EvidenceFile> list(String runId);
    StoredObject load(String runId, String filename);
    void delete(String runId, String filename);

    record StoredObject(byte[] content, String contentType, String originalFilename) {}
}
