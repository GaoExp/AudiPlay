## [1.0.0.1.2] - 2026-06-06

### 🔢 Version
- versionCode: 6
- versionName: 1.0.0.1.2

### ✨ Fitur Baru
- Format Audio di Settings — filter file berdasarkan ekstensi format (Audio, Rekaman, MIDI, Video, Stream)

### ♻️ Perubahan Fitur
- mini player selalu tampil di bawah meski tidak ada lagu diputar
- tampilan kosong: judul "Mini Player", subjudul "Pilih lagu untuk diputar"
- Scan All Audio diubah dari tombol klik jadi SwitchCompat — saat ON, semua audio dipindai tanpa filter folder; saat OFF, hanya folder yang disertakan dipindai
- Folder Disertakan otomatis dinonaktifkan (disabled/greyed out) saat Scan All Audio aktif
- Hapus opsi Mode Ulang Default di Pengaturan
- Jaga Layar Tetap Nyala ditambahi informasi "Membatalkan batas waktu layar"
- Tambah opsi Putar Audio Diatas Aplikasi Lain — toggle + informasi "Tidak ada gangguan pemutaran"; implementasi: skip audio focus request saat ON, abandon fokus, pause kalah fokus saat OFF
- Tambah tombol panah bawah (▼) di Now Playing untuk menutup expanded player
- Icon favorit dirapikan — posisi, padding, hapus redundant android:tint
- Navigasi ganti lagu: dari ketukan sisi (previous/next) jadi swipe kiri/kanan di album art
- Tombol panah bawah & favorit di Now Playing tidak bisa ditekan — diperbaiki (mini_player_overlay disembunyikan saat expanded)

### 🗒️ File Added
- `app/src/main/res/layout/dialog_audio_formats.xml`

### 🐞 Bug Fixes
- lagu berhenti saat keluar app — `startForeground()` dipanggil di `playQueue()` SEBELUM player mulai (hindari `ForegroundServiceStartNotAllowedException` di Android 12+)
- lagu berhenti sendiri & now playing kosong (service restart) — simpan track terakhir ke SharedPreferences, pulihkan saat service restart
- file audio corrupt/codec error — ExoPlayer skip ke track berikutnya (onPlayerError)
- queue habis (repeat off) — togglePlayPause restart dari awal, bukan diam

### ✏️ File Changed
- `app/src/main/java/exp/miniplayer/utils/PreferencesManager.java`
- `app/src/main/java/exp/miniplayer/utils/MusicScanner.java`
- `app/src/main/java/exp/miniplayer/player/MusicPlayer.java`
- `app/src/main/java/exp/miniplayer/service/MusicService.java`
- `app/src/main/java/exp/miniplayer/ui/settings/SettingsViewModel.java`
- `app/src/main/java/exp/miniplayer/ui/settings/SettingsFragment.java`
- `app/src/main/res/layout/fragment_settings.xml`
- `app/src/main/res/values/strings.xml`
- `app/src/main/res/layout/view_now_playing_sheet.xml`
- `app/src/main/java/exp/miniplayer/MainActivity.java`
- `app/src/main/res/drawable/ic_favorite.xml`
- `app/src/main/res/drawable/ic_favorite_border.xml`
- `app/src/main/res/values/arrays.xml`

## [1.0.0.1.1] - 2026-06-06

### 🔢 Version
- versionCode: 5
- versionName: 1.0.0.1.1

### ✨ Fitur Baru
- implementasi Scan All Audio (trigger re-scan manual dari Settings)
- implementasi Included Folders (filter folder yang dipindai)
- implementasi Excluded Folders (folder yang dilewati saat scan)

### ♻️ Perubahan Fitur
- ikon tutup Now Playing dari × (close) jadi ▼ (panah bawah)
- judul & artis lagu dipindah ke toolbar Now Playing (antara ikon tutup dan suka)
- semua string UI diubah ke bahasa Indonesia
- Scan All Audio, Included Folders, Excluded Folders kini berfungsi
- tambah tombol prev/next di mini player bawah
- swipe-down drag untuk tutup Now Playing (seret judul ke bawah)
- Now Playing (Activity) + mini player digabung jadi Bottom Sheet tunggal di MainActivity
- kontrol pemutaran (shuffle, repeat, seekbar, favorit, progress) pindah ke MainActivity
- klik mini player buka expanded player (bukan Activity baru)
- seret expanded player ke bawah untuk tutup (collapse ke mini player)

### 🔧 Optimasi & Penyesuaian
- tambah izin WAKE_LOCK
- folder picker kini pakai SAF (file manager) bukan input teks manual
- Now Playing jadi overlay di atas daftar lagu (transisi slide up/down)
- drag-to-dismiss dengan animasi di Now Playing

### 🗒️ File Added
- `app/src/main/res/drawable/ic_arrow_down.xml`
- `app/src/main/res/drawable/drag_handle_background.xml`
- `app/src/main/res/layout/dialog_folder_list.xml`
- `app/src/main/res/layout/item_folder.xml`
- `app/src/main/res/layout/dialog_folder_input.xml`
- `app/src/main/res/anim/slide_in_up.xml`
- `app/src/main/res/anim/slide_out_down.xml`
- `app/src/main/res/anim/stay.xml`

### ✏️ File Changed
- `app/src/main/AndroidManifest.xml`
- `app/src/main/res/values/strings.xml`
- `app/src/main/res/layout/fragment_settings.xml`
- `app/src/main/res/layout/activity_main.xml`
- `app/src/main/res/layout/view_now_playing_sheet.xml`
- `app/src/main/res/values/themes.xml`
- `app/src/main/res/values/dimens.xml`
- `app/src/main/java/exp/miniplayer/utils/PreferencesManager.java`
- `app/src/main/java/exp/miniplayer/utils/MusicScanner.java`
- `app/src/main/java/exp/miniplayer/data/AudioRepository.java`
- `app/src/main/java/exp/miniplayer/ui/settings/SettingsViewModel.java`
- `app/src/main/java/exp/miniplayer/ui/settings/SettingsFragment.java`
- `app/src/main/java/exp/miniplayer/MainActivity.java`
- `app/src/main/java/exp/miniplayer/service/MusicService.java`
- `app/src/main/java/exp/miniplayer/ui/songs/SongsFragment.java`
- `app/src/main/java/exp/miniplayer/ui/favorites/FavoritesFragment.java`
- `app/src/main/java/exp/miniplayer/ui/playlist/PlaylistDetailActivity.java`

### 🔥 File Removed
- `app/src/main/java/exp/miniplayer/ui/nowplaying/NowPlayingActivity.java`
- `app/src/main/java/exp/miniplayer/ui/nowplaying/NowPlayingViewModel.java`
- `app/src/main/res/layout/activity_now_playing.xml`
- `app/src/main/res/layout/view_mini_player.xml`

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
