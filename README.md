# SureFix Lite — evidence-service

| | |
|---|---|
| **Student Name** | Shehan Anujaya |
| **Student Number** | __STUDENT_NUMBER__ |
| **Slack Handle** | __SLACK_HANDLE__ |
| **GCP Project ID** | `surefix-eca` |

## Project Description
Stores **evidence files** (screenshots, traces, logs) for a run in the **Google Cloud Storage**
bucket `surefix-eca-evidence` under `runs/{runId}/…`. Uses Application Default Credentials
(the VM service account on GCP).

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/api/v1/evidence` | multipart `file` + `runId` → `{runId, filename, contentType, size, url}` |
| `GET` | `/api/v1/evidence?runId=` | List files of a run |
| `GET` | `/api/v1/evidence/{runId}/{filename}` | Download / preview |
| `DELETE` | `/api/v1/evidence/{runId}/{filename}` | Delete |

Part of the [services parent repository](https://github.com/shehan-anujaya/surefix-services).

## Technology Stack
Java 25 · Spring Boot 4.0.8 · Google Cloud Storage client · Spring Cloud Config/Eureka client 2025.1.3 · Micrometer Tracing/Zipkin · PM2

## Setup / Getting Started
```bash
gcloud auth application-default login     # local credentials for the bucket
mvn spring-boot:run                       # http://localhost:8083/api/v1/evidence?runId=demo
mvn -DskipTests package                   # target/evidence-service.jar
```
Bucket name and upload limits come from the Config Server (`evidence-service.yaml`).
