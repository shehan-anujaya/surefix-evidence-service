# SureFix Lite — evidence-service

| | |
|---|---|
| **Student Name** | Shehan Anujaya |
| **Student Number** | 241711072 |
| **Slack Handle** | U0BKHA1JHNC |
| **GCP Project ID** | `surefix-eca` |

## Project Description
Stores **evidence files** (screenshots, HAR traces, logs) for a run in the **Google Cloud Storage**
bucket `surefix-eca-evidence` under `runs/{runId}/{uuid}{.ext}`. Object names are generated server-side
(`ObjectKeys`) so uploads can never escape the run prefix; the original filename is kept as object
metadata. Uses Application Default Credentials (the VM service account on GCP).

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/api/v1/evidence` | multipart `file` + `runId` → `{runId, filename, contentType, size, uploadedAt, url}` (max 20 MB → `413`) |
| `GET` | `/api/v1/evidence?runId=` | List files of a run, newest first |
| `GET` | `/api/v1/evidence/{runId}/{filename}?download=true` | Stream / download (`Content-Disposition` when `download=true`) |
| `DELETE` | `/api/v1/evidence/{runId}/{filename}` | Delete |

Cloud Storage failures are reported as `502` with the uniform `ApiError` body.

Part of the [services parent repository](https://github.com/shehan-anujaya/surefix-services).

## Technology Stack
Java 25 · Spring Boot 4.0.8 · Google Cloud Storage client · Spring Cloud Config/Eureka client 2025.1.3 · Micrometer Tracing/Zipkin · JUnit 5 · PM2

## Setup / Getting Started
```bash
gcloud auth application-default login     # local credentials for the bucket
mvn test                                  # unit tests
mvn spring-boot:run                       # http://localhost:8083/api/v1/evidence?runId=demo
mvn -DskipTests package                   # target/evidence-service.jar
```
Bucket name and upload limits come from the Config Server (`evidence-service.yaml`).
