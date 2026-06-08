# AGENTS.md — Pedoman AI Project
 
Dokumen ini adalah aturan kerja untuk AI agent yang memodifikasi project. 
 
Gunakan bahasa Indonesia. 
 
Tujuan: 
- konsistensi versi 
- konsistensi dokumentasi (CHANGELOG, README, STRUKTUR, PANDUAN)
- mudah dibaca manusia 
- mengurangi perilaku AI yang terlalu verbose atau over-engineering 
 
--- 
 
## 1. Versioning 
 
File: 
`app/build.gradle` 
 
### versionCode         
 
WAJIB: 
- integer 
- selalu +1 setiap update 
- tidak pernah reset 
 
### versionName 
 
Format: 
 
`major.removed.restored.minor.patch` 
 
Contoh: 
 
`1.1.1.15.4`  
   
### IMPORTANT  
     
Project ini TIDAK menggunakan semantic versioning standar.
     
JANGAN menerapkan aturan semantic versioning standar pada project ini.
     
Contoh yang VALID:
     
`1.1.1.15.4` → `2.1.1.16.0`
     
BUKAN:
     
`1.1.1.15.4` → `2.0.0.0.0`
 
### Arti Komponen 
 
major: 
- milestone besar 
- generasi project 
- perubahan arsitektur besar 
- boleh naik kapan diperlukan 
- selalu dianggap sebagai feature release
    
removed: 
 
Counter historis fitur yang pernah dihapus, deprecated, dinonaktifkan, atau dipensiunkan. 
 
NAIK saat: 
- fitur dihapus 
- fitur deprecated 
- fitur disable permanen 
- fitur diganti total 
 
TIDAK PERNAH TURUN. 
 
restored: 
 
Counter historis fitur yang pernah dikembalikan setelah sebelumnya dihapus atau dinonaktifkan. 
 
NAIK saat: 
- fitur lama kembali 
- fitur deprecated diaktifkan lagi 
- fitur retired dipulihkan 
 
TIDAK PERNAH TURUN. 
 
minor: 
 
Counter feature release. 
 
NAIK saat: 
- fitur baru ditambahkan 
- fitur besar ditambahkan 
- major naik
- removed naik
- restored naik
- perubahan feature-level besar

patch: 
 
Digunakan untuk: 
- bugfix 
- optimization 
- maintenance 
- refactor kecil 
- dependency update 
- UI cleanup 
 
### Aturan 
 
- patch reset → `0` saat minor naik 
- minor +1 saat major naik 
- removed TIDAK reset 
- restored TIDAK reset 
 
### Contoh 
 
Awal: 
 
`1.1.1.15.4` 
 
arti: 
- major 1 
- 1 fitur pernah dihapus 
- 1 fitur pernah dipulihkan 
- feature release ke-15 
- patch 4
 
Tambah fitur: 
 
`1.1.1.16.0` 
 
minor +1
patch reset 
 
Fitur dihapus: 
 
`1.2.1.17.0` 
 
removed +1
minor +1
patch reset 
 
Fitur kembali: 
 
`1.2.2.18.0` 
 
restored +1
minor +1
patch reset 
 
Bugfix: 
 
`1.2.2.18.1` 
 
patch +1 
 
Major release: 
 
`3.2.2.19.0` 
 
major +1
minor +1
patch reset
     
### Algoritma Increment

Feature baru:
minor+1
patch=0

Feature removed:
removed+1
minor+1
patch=0

Feature restored:
restored+1
minor+1
patch=0

Bugfix:
patch+1

Major release:
major+1
minor+1
patch=0
---
 
## 2. CHANGELOG

CHANGELOG adalah riwayat perubahan release.

File:
- `CHANGELOG.md` (root) — markdown, untuk GitHub
- `app/src/main/assets/CHANGELOG.txt` — plain text, untuk in-app

Entry baru:

WAJIB ditaruh di paling atas. 

Format (.md):

```md
## [X.X.X.X.X] - YYYY-MM-DD 
``` 

Gunakan section sesuai kebutuhan. Urutan section WAJIB mengikuti urutan berikut:

```
✨ Fitur Baru
🚮 Fitur Dihapus
📥 Fitur Dipulihkan
♻️ Perubahan Fitur
🔧 Optimasi & Penyesuaian
🐞 Bug Fixes
💡 Catatan
🗒️ File Added
✏️ File Changed
🔥 File Removed
🔢 Version
```

Daftar lengkap dengan deskripsi ada di **Section Changelog** README.md / README.txt.

### Aturan 

WAJIB: 
- semua file yang benar-benar diubah dicatat 
- update versionCode dicatat 
- update versionName dicatat 
- khusus untuk build.gradle, changelog dan readme tidak perlu dicatat (dikecualikan)
- section WAJIB ditulis sesuai urutan di atas

JANGAN: 
- menambah changelog untuk perubahan trivial 
- menulis penjelasan terlalu panjang 
- membuat subsection yang tidak perlu 
- mengulang detail implementasi kecil 
- mengubah urutan section

Ringkas, faktual, langsung ke perubahan. 
 
--- 
 
## 3. Dokumentasi (.md root ↔ .txt app/src/main/assets/)

Dokumentasi terdiri dari 4 pasang file yang harus disinkronkan secara manual (DEVELOPMENT digabung ke README, TENTANG digabung ke README):

| Root (.md)          | Assets (.txt) (app/src/main/assets/) | Isi                              |
|---------------------|--------------------------------------|----------------------------------|
| README.md           | README.txt                           | Ringkasan fitur + development    |
| STRUKTUR.md         | STRUKTUR.txt                         | Struktur project & deskripsi file|
| PANDUAN.md          | PANDUAN.txt                          | Panduan penggunaan lengkap       |
| CHANGELOG.md        | CHANGELOG.txt                        | Riwayat perubahan release        |

### Format file

- `.md` (root) — markdown, untuk dibaca di GitHub dengan rendered view
- `.txt` (`app/src/main/assets/`) — plain text, untuk dibaca **di dalam aplikasi** via DocumentationActivity

Kedua pasangan harus memiliki **isi yang sama**, hanya formatnya berbeda.

### DocumentationActivity

Aplikasi memiliki `DocumentationActivity` yang dapat diakses dari **Pengaturan → Lihat Dokumentasi**.
Activity ini membaca file `.txt` dari `app/src/main/assets/` dan menampilkannya sebagai plain text.

Saat menambah/menghapus file dokumentasi:
1. update `DocumentationActivity.java` (array `DOCS`)
2. update `strings.xml` (title + subtitle strings)
3. update `fragment_settings.xml` jika subtitle perlu diubah

### README / PANDUAN (Dokumentasi pengguna akhir)

WAJIB update bila: 
- ada fitur baru 
- ada fitur dihapus 
- ada fitur dipulihkan 
- ada perubahan struktur project 
- ada perubahan UI/UX besar 
- Current Version berubah 
- Last Updated berubah 

Update hanya jika relevan: 
- daftar fitur 
- requirement 
- permission 

### STRUKTUR

WAJIB update bila:
- ada file/direktori baru
- ada file/direktori dihapus
- ada perubahan struktur package

### CHANGELOG

Update WAJIB setiap ada perubahan kode.
Aturan penulisan ada di **Section 2 — CHANGELOG.txt** di atas.
 
--- 
 
## 4. Workflow AI

Siklus kerja: **setelah rilis → edit → rilis → edit → ...**

### Aturan Dasar

- **JANGAN** commit atau tag kecuali diperintahkan secara eksplisit.
- **JANGAN** membuat commit kosong atau tanpa perubahan kode.

### 4.1 Setelah Rilis (commit & tag selesai)

Setelah user memerintahkan rilis dan commit+tag sukses terbentuk, **versi saat ini dianggap final**.

Perubahan berikutnya WAJIB menggunakan **versi baru**:

1. update versionCode (+1)
2. update versionName sesuai Algoritma Increment (Section 1)
3. buat entry baru di `CHANGELOG.md` + `app/src/main/assets/CHANGELOG.txt` di paling atas
4. catat perubahan pada entry baru tersebut
5. update pasangan dokumentasi yang relevan (.md root + `app/src/main/assets/`)
6. jika ada file dokumentasi ditambah/dihapus, update `DocumentationActivity.java` (array `DOCS`) + `strings.xml`
7. **JANGAN commit**
8. **JANGAN tag**

### 4.2 Edit Biasa (versi sudah ada)

Jika sudah ada entry versi yang sedang dikerjakan (belum di-commit/tag):

1. update kode
2. catat perubahan di `CHANGELOG.md` + `app/src/main/assets/CHANGELOG.txt` pada **entry versi yang sedang dikerjakan**
3. update pasangan dokumentasi yang relevan (.md root + `app/src/main/assets/`):
   - `README.md` + `app/src/main/assets/README.txt`
   - `STRUKTUR.md` + `app/src/main/assets/STRUKTUR.txt`
   - `PANDUAN.md` + `app/src/main/assets/PANDUAN.txt`
4. jika ada file dokumentasi ditambah/dihapus, update `DocumentationActivity.java` (array `DOCS`) + `strings.xml`
5. **JANGAN** update versionCode / versionName (sudah diatur di langkah 4.1)
6. **JANGAN** commit
7. **JANGAN** tag

### 4.3 Rilis (hanya saat diperintahkan)

Saat user memerintahkan commit & tag:

1. pastikan kode sudah lengkap
2. **JIKA** versi sudah dibuat (langkah 4.1 sudah dijalankan sebelumnya):
   - lewati update versionCode / versionName
   - lewati update CHANGELOG (entry sudah ada)
3. **JIKA** versi belum dibuat (lupa atau skip):
   - update versionCode (+1)
   - update versionName sesuai Algoritma Increment
   - buat entry CHANGELOG baru
4. update semua pasangan dokumentasi yang relevan — pastikan semuanya sinkron
5. jika ada file dokumentasi ditambah/dihapus, update `DocumentationActivity.java` (array `DOCS`) + `strings.xml`
6. `git add -A && git commit`
7. `git tag vX.X.X.X.X`
8. beri tahu user bahwa tinggal `git push`
 
--- 
 
## 5. Perilaku AI 
 
Penerapan berlaku kepada semua Agent AI yang terlibat dalam pengerjaan Project, terutama untuk model Agent GitHub CoPilot (important) 
Kerjakan hanya sesuai request user. 
 
JANGAN: 
- melakukan refactor tanpa diminta 
- mengubah file di luar scope request 
- mengaudit seluruh project tanpa diminta 
- membuat checklist panjang maupun singkat untuk request sederhana (important) 
- memberi penjelasan panjang bila tidak diminta 
 
Utamakan: 
- perubahan minimal 
- perubahan terfokus 
- jawaban singkat 
- edit seperlunya 
- solusi langsung bisa dipakai 
 
Default behavior: 
 
jangan terlalu rajin. 
 
Jika user meminta perubahan kecil, 
kerjakan perubahan kecil. 
 
### Chat Response Rules 
 
Default response style: 
- singkat 
- fokus 
- actionable 
 
JANGAN: 
- menjelaskan langkah yang tidak dilakukan 
- membuat audit project tanpa diminta 
- menjelaskan teori panjang untuk perubahan sederhana 
- menambahkan rekomendasi besar di luar request tanpa diminta
 
Jika request sederhana = jawab sederhana.