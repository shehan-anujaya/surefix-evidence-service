package lk.ijse.eca.surefix.evidence.controller;

import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import lk.ijse.eca.surefix.evidence.dto.EvidenceFile;
import lk.ijse.eca.surefix.evidence.service.StorageService;

@RestController
@RequestMapping("/api/v1/evidence")
public class EvidenceController {

    private final StorageService storage;

    public EvidenceController(StorageService storage) {
        this.storage = storage;
    }

    @PostMapping
    public ResponseEntity<EvidenceFile> upload(@RequestParam String runId, @RequestParam("file") MultipartFile file) {
        return ResponseEntity.status(HttpStatus.CREATED).body(storage.upload(runId, file));
    }

    @GetMapping
    public List<EvidenceFile> list(@RequestParam String runId) {
        return storage.list(runId);
    }

    @GetMapping("/{runId}/{filename}")
    public ResponseEntity<byte[]> get(@PathVariable String runId, @PathVariable String filename,
                                      @RequestParam(defaultValue = "false") boolean download) {
        StorageService.StoredObject obj = storage.load(runId, filename);
        MediaType type = obj.contentType() != null ? MediaType.parseMediaType(obj.contentType()) : MediaType.APPLICATION_OCTET_STREAM;
        ResponseEntity.BodyBuilder response = ResponseEntity.ok().contentType(type)
                .header(HttpHeaders.CACHE_CONTROL, "private, max-age=300");
        if (download) {
            response.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"");
        }
        return response.body(obj.content());
    }

    @DeleteMapping("/{runId}/{filename}")
    public ResponseEntity<Void> delete(@PathVariable String runId, @PathVariable String filename) {
        storage.delete(runId, filename);
        return ResponseEntity.noContent().build();
    }
}
