## [1.0.0.1.0] - 2026-06-06

### 🔢 Version
- versionCode: 4
- versionName: 1.0.0.1.0

### ✨ Fitur Baru
- migrasi navigasi dari Bottom Navigation Bar ke Navigation Drawer (sidebar)
- tambah Toolbar dengan hamburger icon

### ♻️ Perubahan Fitur
- warna diubah ke palet dark theme Material3 sepenuhnya
- hapus folder values-night (tidak terpakai)

### 🐞 Bug Fixes
- fix force close SongsFragment — cast SearchView pakai instanceof

### ✏️ File Changed
- `MainActivity.java`
- `SongsFragment.java`
- `activity_main.xml`
- `strings.xml`
- `app/src/main/res/values/colors.xml`
- `STRUKTUR.md`

## [1.0.0.0.2] - 2026-06-06

### 🔢 Version
- versionCode: 3
- versionName: 1.0.0.0.2

### 🐞 Bug Fixes
- hapus `fallbackToDestructiveMigration()` agar data tidak terhapus saat migrasi
- `previous()` sekarang restart dari 10 detik, bukan langsung loncat lagu
- adapter song tidak dibuat ulang setiap data change
- reset posisi & durasi saat queue habis

### ✏️ File Changed
- `AppDatabase.java`
- `MusicPlayer.java`
- `SongAdapter.java`
- `SongsFragment.java`
- `FavoritesFragment.java`
- `NowPlayingViewModel.java`

## [1.0.0.0.1] - 2026-06-06

### 🔢 Version
- versionCode: 2
- versionName: 1.0.0.0.1

### 🔧 Optimasi & Penyesuaian
- hapus `aapt2FromMavenOverride` dari gradle.properties untuk kompatibilitas CI

### ✏️ File Changed
- `gradle.properties`
- `app/build.gradle`

## [1.0.0.0.0] - 2026-06-06

### 🔢 Version
- versionCode: 1
- versionName: 1.0.0.0.0

### ✨ Fitur Baru
- inisialisasi project Mini Player
- pemutaran audio dengan Media3 ExoPlayer
- foreground service untuk background playback
- daftar lagu dengan scanning audio lokal
- pencarian dan pengurutan lagu
- fitur favorit (Room database)
- fitur playlist (buat, rename, hapus, kelola lagu)
- now playing screen dengan kontrol pemutaran lengkap
- mode shuffle, repeat all, repeat one
- dark mode dan follow system theme
- notifikasi pemutaran dengan kontrol
- pengaturan aplikasi (tema, repeat default, folder scan)
- izin runtime untuk audio dan notifikasi
- dukungan Bluetooth AVRCP

### 🗒️ File Added
- seluruh struktur project awal

---
