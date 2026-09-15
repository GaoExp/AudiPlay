# Perbaikan Fungsional

**Status:** Rencana — belum dikerjakan
**Tujuan:** Memperbaiki bug yang langsung terasa pengguna setelah aplikasi dihidupkan, tanpa mengubah arsitektur besar.

---

## F-01. Queue lagu salah saat pencarian aktif

**Lokasi:** `app/src/main/java/exp/miniplayer/ui/songs/SongsFragment.java:157`

`onItemClick(Audio audio, int position)` selalu memakai seluruh daftar:
```java
List<Audio> queue = repository.getCachedAudio();
```
padahal daftar yang ditampilkan saat ini adalah hasil filter `searchAudio(query)`. Saat keyword pencarian aktif, posisi yang diklik tidak cocok dengan posisi di daftar penuh → lagu yang keluar dari queue tidak sesuai (berpotensi memainkan lagu lain).

**Rancangan fix:**
- Umumkan queue pada daftar yang **sedang tampil** (`SongsFragment`): jika filter/search aktif, queue = hasil `searchAudio`, jika tidak = `getCachedAudio`.
- Jaga konsisten dengan `SongsViewModel.getSongs()` yang sudah menerapkan filter (SongsViewModel.java:54-57).

**File diubah:** `SongsFragment.java`.

---

## F-02. Scan audio & query DB sinkron di UI thread

**Lokasi:**
- `app/src/main/java/exp/miniplayer/ui/songs/SongsViewModel.java:29-32` — `repository.scanAudio()` (query MediaStore penuh) dipanggil langsung di thread utama.
- `app/src/main/java/exp/miniplayer/ui/playlist/PlaylistFragment.java` — query Room sinkron di observer (jalankan di UI thread) saat `getCachedAudio()` sudah memicu `scanAudio()`.

Dengan ratusan/menyusun lagu, UI bisa jank/ANR saat scan pertama atau saat buka tab Playlist.

**Rancangan fix:**
- Jalankan `scanAudio()` (dan operasi DB berat) di background thread (mis. `ExecutorService`/`AsyncTask` lama yang sudah ada, atau pindah ke pattern coroutine/executor milik project — jangan mengimpor library baru tanpa konfirmasi).
- `PlaylistFragment`: pindahkan query Room ke thread background lalu post hasil ke UI.

**File diubah:** `SongsViewModel.java`, `PlaylistFragment.java` (dan kemungkinan `AudioRepository.java` jika perlu helper async).

---

## F-03. Foreground service dilewati tanpa izin notifikasi (Android 13+)

**Lokasi:** `app/src/main/java/exp/miniplayer/service/MusicService.java`
- `playQueue()` — cek izin `POST_NOTIFICATIONS` lalu baris 123-127: jika izin belum diberikan (Tiramisu+), **tidak** memanggil `startForeground`.
- `updateNotification()` — baris 245-249: `return` dini tanpa `startForeground`.
- `onPlayStateChanged(boolean)` — baris 317-322: `return` dini tanpa `startForeground`.

Akibat: pemutaran berjalan tanpa service di-foreground → service bisa dibunuh sistem di background / pemutaran berhenti, dan notifikasi media tidak muncul. Izin `POST_NOTIFICATIONS` hanya diminta saat first-run (`PermissionHelper`) dan bisa diskip "Nanti".

**Catatan (penting):** pada Android 13+, `startForeground` untuk tipe media **tidak** mensyaratkan `POST_NOTIFICATIONS`; izin itu hanya memengaruhi visibilitas notifikasi. Jadi gating `startForeground` dengan cek izin adalah **salah arah** — yang tidak tampil hanyalah notifikasi, bukan service-nya.

**Rancangan fix:**
- Selalu panggil `startForeground` (dengan `foregroundServiceType` media play/notification) terlepas dari izin notifikasi.
- Hapus gating `checkSelfPermission` pada jalur `startForeground`.
- Untuk tampilan notifikasi: jika izin belum diberikan, tetap mulai foregrasound; notifikasi muncul setelah izin diberikan. Pertimbangkan meminta ulang `POST_NOTIFICATIONS` saat interaksi play.

**File diubah:** `MusicService.java`, kecil kemungkinan `PermissionHelper`/`MainActivity`.

---

## F-04. `MediaStore.DATA` deprecated & filter ekstensi terlalu longgar

**Lokasi:** `app/src/main/java/exp/miniplayer/utils/MusicScanner.java:29,44,107,123,208`

1. Akses path via kolom `MediaStore.Audio.Media.DATA` (deprecated sejak API 29). Pada sebagian device/Android 13+ nilai bisa kosong → lagu ter-skip tanpa sebab, atau path tidak valid saat interpretasi.
2. Filter "audio lainnya" (`OtherAudioFragment`): `scanOtherAudio` hanya men-skip file berekstensi musik (`MusicScanner.java:53`), jadi file **tanpa ekstensi** ikut tampil di daftar "Lainnya" padahal bukan audio.

**Rancangan fix:**
- Untuk keperluan pemutaran, prioritaskan URI (`ContentResolver.Uri`) dari `_ID` daripada path `DATA`; gunakan `DATA` hanya sebagai cadangan/pengelompokan.
- Perketat filter: file tanpa ekstensi tidak dimasukkan ke daftar audio/lainnya kecuali MIME-nya audio.

**File diubah:** `MusicScanner.java`, dan penyesuaian konsumsi path di `AudioRepository`/adaptor bila diperlukan.

---

## Checklist

- [ ] F-01 queue memakai daftar tampilan (search aktif) → verifikasi main lagu sesuai urutan
- [ ] F-02 scan & query DB tidak lagi jalan di UI thread (tes dengan banyak lagu)
- [ ] F-03 pemutaran berjalan walau `POST_NOTIFICATIONS` belum diberikan (Android 13+)
- [ ] F-04 daftar lagu tidak kehilangan lagu; file tanpa ekstensi tidak masuk daftar "Lainnya"