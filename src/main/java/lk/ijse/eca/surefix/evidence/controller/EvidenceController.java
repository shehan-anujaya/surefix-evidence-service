package lk.ijse.eca.surefix.evidence.controller;

import java.util.List;

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
import org.springframework.web.server.ResponseStatusException;

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
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(storage.upload(runId, file));
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @GetMapping
    public List<EvidenceFile> list(@RequestParam String runId) {
        return storage.list(runId);
    }

    @GetMapping("/{runId}/{filename}")
    public ResponseEntity<byte[]> get(@PathVariable String runId, @PathVariable String filename) {
        try {
            StorageService.StoredObject obj = storage.load(runId, filename);
            MediaType type = obj.contentType() != null ? MediaType.parseMediaType(obj.contentType()) : MediaType.APPLICATION_OCTET_STREAM;
            return ResponseEntity.ok().contentType(type).body(obj.content());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @DeleteMapping("/{runId}/{filename}")
    public ResponseEntity<Void> delete(@PathVariable String runId, @PathVariable String filename) {
        try {
            storage.delete(runId, filename);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }
}
