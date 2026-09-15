# Perbaikan Build & Env

**Status:** Rencana — belum dikerjakan
**Tujuan:** Membuat project AudiPlay bisa di-build lokal di lingkungan ini (JDK 21, SDK `/opt/android_sdk`, host aarch64) dan menyiapkan jalur signing yang benar.

---

## 1. Lingkungan Tersedia (hasil cek)

| Komponen | Nilai |
|----------|-------|
| Mesin | `aarch64` (ARM64) |
| JDK | `/opt/java/jdk-21.0.12.1+1` (env `JAVA_HOME` kosong) |
| Android SDK | `/opt/android_sdk` (platform android-29 & android-35, build-tools 29.0.3 & 35.0.0) |
| aapt2 sistem | `/usr/bin/aapt2` → `/usr/lib/android-sdk/build-tools/debian/aapt2` (ARM64 asli) |
| Wrapper | Gradle 8.13 (dist sudah ter-cache) |
| AGP | 8.12.0 |

---

## 2. Masalah & Solusi

### 2.1 SDK & JDK tidak ditemukan oleh Gradle

`local.properties` TIDAK ada, dan env `ANDROID_HOME`/`JAVA_HOME` kosong.

**Fix:** buat `local.properties` (proyek):
```
sdk.dir=/opt/android_sdk
```
Saat invoke gradle: `JAVA_HOME=/opt/java/jdk-21.0.12.1+1` (atau `org.gradle.java.home` di `gradle.properties` bila mau permanen).

### 2.2 `aapt2` untuk host aarch64

AGP men-download `aapt2` (artifact maven) yang umumnya berisi binary x86_64 → gagal di ARM64. Sebelum penyegaran, `gradle.properties` AudiPlay berisi `android.aapt2FromMavenOverride=` (kosong) yang membuat AGP memakai aapt2 sistem (`/usr/bin/aapt2`, sudah terpasang untuk ARM64). File `gradle.properties` kini disalin dari FTxT yang **tidak** punya baris ini.

**Fix (pilih satu):**
- A: kembalikan `android.aapt2FromMavenOverride=` (kosong) pada `gradle.properties`.
- B: di `app/build.gradle`, override `aapt2` via `android { aaptOptions... }` (sesuai pendekatan FTxT bila dipakai).

> Baris ini **tidak dicatat di CHANGELOG** (aturan AGENTS.md §2.7: `build.gradle`/`gradle.properties` dilarang dicatat).

### 2.3 Signing release

`app/release.jks` TIDAK ada — signing release memakai keystore tsb + env `KEYSTORE_PASSWORD`/`KEY_ALIAS`/`KEY_PASSWORD` (diisi CI dari `KEYSTORE_BASE64`). Debug dan install lokal tidak terpengaruh.

**Fix:** tidak perlu apa-apa untuk develop lokal. Untuk distribusi, pastikan pipeline/secret masih tersedia, atau simpan keystore di lingkungan yang aman bila mau `assembleRelease` manual.

### 2.4 Heap Gradle

`gradle.properties` sekarang `org.gradle.jvmargs=-Xmx800m`. Project Java murni (tanpa Kotlin), jadi menghemat memori — aman. Bila build OOM, naikkan ke 1536–2048m (sesuai kemampuan mesin).

---

## 3. File yang Diubah

| File | Tindakan |
|------|----------|
| `local.properties` (baru) | Tambah `sdk.dir=/opt/android_sdk` |
| `gradle.properties` | Opsional restore `android.aapt2FromMavenOverride=` / `org.gradle.java.home` |
| `app/build.gradle` | Hanya bila pakai opsi 2.2.B |

---

## 4. Verifikasi Build

1. `JAVA_HOME=/opt/java/jdk-21.0.12.1+1 ./gradlew :app:compileDebugJavaWithJavac` → kompilasi Java OK.
2. `./gradlew :app:assembleDebug` → APK `AudiPlay-v...-debug.apk` dihasilkan.
3. Cek `aapt2` terpakai versi sistem bila opsi 2.2.A dipasang.

---

## 5. Checklist

- [ ] `local.properties` dibuat
- [ ] aapt2 override dipasang kembali (opsi A atau B)
- [ ] `assembleDebug` sukses
- [ ] (opsional) `assembleRelease` dipahami jalur signingnya