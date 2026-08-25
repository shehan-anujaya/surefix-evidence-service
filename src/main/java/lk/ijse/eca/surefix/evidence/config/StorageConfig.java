package lk.ijse.eca.surefix.evidence.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;

@Configuration
public class StorageConfig {

    /** Cloud Storage client authenticated with Application Default Credentials (the VM service account on GCP). */
    @Bean
    Storage storage() {
        return StorageOptions.getDefaultInstance().getService();
    }
}
