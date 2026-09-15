# Daftar Perbaikan AudiPlay

**Status:** Indeks — semua item masih rencana
**Tanggal:** 2026/09/15
**Konteks:** Hasil analisa menyeluruh project AudiPlay (Java, ``exp.miniplayer``) setelah lama terbengkalai, untuk dihidupkan kembali. Detail tiap item ada di dokumen masing-masing.

---

## Ringkasan Eksekutif

Project Java murni, struktur bersih, semua resource (R.id/R.drawable/R.string/R.menu/R.plurals) terverifikasi cocok dengan kode. Halangan utama bukan struktur, tapi **lingkungan build** (SDK path, aapt2 untuk aarch64, keystore) dan **beberapa bug fungsional** yang akan langsung terasa pengguna. Prioritaskan pemenuhan "build jalan dulu", baru bug, baru rapikan dokumen/versi.

---

## Daftar Item

| Kode | Item | Prioritas | Status |
|------|------|-----------|--------|
| B-01 | Build & env (local.properties, aapt2 aarch64, keystore) | **Tinggi** | Rencana |
| F-01 | Queue salah saat pencarian lagu aktif | **Tinggi** | Rencana |
| F-02 | Scan audio & query DB sinkron di UI thread | **Tinggi** | Rencana |
| F-03 | Foreground service terlewat tanpa izin notifikasi (Android 13+) | **Tinggi** | Rencana |
| F-04 | `MediaStore.DATA` deprecated & filter ekstensi longgar | Sedang | Rencana |
| K-01 | FileObserver per subfolder + path hardcoded | Sedang | Rencana |
| K-02 | `allowMainThreadQueries()` & schema Room cleanup | Sedang | Rencana |
| D-01 | Rapikan state git & format dokumen/versi | Sedang | Rencana |

**Status per dokumen:**
- [B-01] `PRIORITAS/PERBAIKAN_BUILD_DAN_ENV.md`
- [F-01..F-04] `PRIORITAS/PERBAIKAN_FUNGSIONAL.md`
- [K-01..K-02] `RENCANA/PERBAIKAN_KETAHANAN.md`
- [D-01] `RENCANA/PERAPIHAN_DOKUMEN_DAN_VERSI.md`

---

## Catatan

- Tidak ada satu pun item yang dikerjakan (semua masih `Rencana`). Item yang selesai dipindah ke `SELESAI/` (dan dihapus dari daftar di sini).
- Semua line-number adalah kondisi kode saat analisa (2026/09/15); bisa bergeser setelah perubahan.