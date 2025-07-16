# 📦 Logger Module - Android Background Log Uploader

A robust background logger system designed with Clean Architecture and Hilt DI. It captures app logs, persists them in a Room database, and uploads them periodically or when buffer thresholds are met — surviving process death, reboots, and crashes.

---

## 🚀 Features

- Writes logs to **Room DB** (vs file, for querying + consistency)
- Periodic upload via **WorkManager**
- Upload retry & batching
- Log purge after successful upload
- Threshold-based trigger with **Flow observer**
- Boot-resilient via **BroadcastReceiver**
- Prevents duplicate uploads using `AtomicBoolean`
- **Modular and testable** using Clean Architecture + SOLID principles

---

## 🧱 Architecture Overview

### Layers
- **Domain Layer**: Use cases and interfaces
- **Data Layer**: Room DB, Repository, Network
- **No UI layer**: Runs headless as background infrastructure

---

## 🔄 Upload Triggers

| Trigger Type        | Description                                                             |
|---------------------|-------------------------------------------------------------------------|
| **Threshold-based** | Logs are uploaded when a configurable threshold (e.g., 50) is reached   |
| **Periodic**        | Scheduled every 15 minutes using `WorkManager`                          |
| **Boot recovery**   | System boots → checks unuploaded logs → triggers upload if necessary    |

---

## 🧠 Process Death & Duplication Handling

- `WorkManager` ensures periodic jobs persist through restarts
- `BroadcastReceiver` is triggered only on `BOOT_COMPLETED` with `Intent.action` check
- `isUploading` flag (global constant) avoids duplicate concurrent uploads

---

## 📆 Dependencies

| Component          | Tech Stack           |
|-------------------|----------------------|
| Logging Storage   | Room DB              |
| Background Tasks  | WorkManager          |
| DI                | Hilt                 |
| Networking        | Retrofit (pluggable) |
| Concurrency       | Coroutines           |
| Reactive Trigger  | Flow                 |

---

## 🔍 Log Flow

1. `Logger.log()` → inserts into DB
2. `observePendingLogsCount()` watches DB changes
3. When threshold met → triggers one time `LogUploader` work
4. `LogUploader` uploads logs via `LogUploadService`
5. On success → marks logs uploaded & purges them
6. On app reboot → `BootCompletedReceiver` recovers pending uploads

---

## 📂 Module Usage

Import `logger` module and initialize in `Application` class:

```kotlin
class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        logger.schedule(this)
    }
}
```

Log messages:

```
logger.log("A sample message")
```

---

## 🔪 Testable Design

- Interfaces (`LoggerRepository`, `LogUploadService`)
- Flows for reactive triggering
- Hilt-injected dependencies
- All I/O done via `Dispatchers.IO`

---

## 🗐 Future Enhancements

- Add exponential backoff on retry
- Optional encryption for logs
- Expose upload result callbacks
- Extendable log filters / types

---

© 2025 • Interview Ready Logger Systems
