# [1.1.1] 2026/09/16 07:11 WITA 36 ***ONGOING***

### 🔖 Deskripsi
> Rilis pemeliharaan pertama setelah project terbengkalai — project dihidupkan kembali dan mulai dipulihkan/diperbarui bertahap.

### ✨ Fitur Baru
- **Riwayat versi lama di Dokumentasi** — Keseluruhan changelog versi sebelumnya diarsipkan ke berkas tersendiri (`old-CHANGELOG.md`) dan dapat dibuka lewat kartu baru "Riwayat Versi Lama" di daftar Dokumentasi.

### ♻️ Perubahan Fitur

### 🔧 Optimasi & Penyesuaian
- **Akses database dipindah ke latar belakang** — Pemindaian audio, perhitungan statistik playlist, aksi favorite (tambah/hapus), pengelolaan playlist (buat/ubah/hapus/tambah/remove lagu), serta impor/ekspor playlist tidak lagi berjalan di thread utama; UI tidak macet saat pustaka besar dan operasi database tidak memblokir tampilan.
- **Pemindaian audio lebih andal di Android modern** — Pemutaran tidak lagi bergantung pada kolom path file yang deprecated (`MediaStore.DATA`); identifikasi lagu memakai URI berbasis ID. Filter daftar "Audio Lainnya" kini menggunakan MIME Type dan tidak lagi memuat file non-audio tanpa ekstensi.
- **Pemantauan file playlist dimodernkan** — Deteksi perubahan file playlist (.m3u/.m3u8) tidak lagi membuat ribuan observer file sistem (sebelumnya berisiko mentok batas inotify dan berhenti bekerja diam-diam); diganti dengan pengecekan perubahan berkala yang meringankan sumber daya. Lokasi Music/Download/Playlists kini disesuaikan dengan penyimpanan perangkat, bukan path hardcoded.

### 🐞 Bug Fixes
- **Pemutaran berhenti di latar belakang (Android 13+)** — Service media kini selalu berpindah ke foreground saat mulai memutar lagu, tidak lagi digantungkan pada izin notifikasi; izin notifikasi hanya memengaruhi visibilitas notifikasi. Pemutaran tetap berjalan walau izin belum diberikan.
- **Pilihan lagu tidak sesuai saat mode pencarian** — Memutar lagu dari hasil pencarian kini memakai daftar yang sedang ditampilkan (hasil filter/sort), bukan seluruh pustaka; posisi lagu yang diputar menjadi benar.