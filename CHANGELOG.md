## [1.1.0.10.1] - 2026-06-09

### ✨ Fitur Baru
- Izin `READ_MEDIA_VIDEO` dan `READ_MEDIA_IMAGES` untuk akses Berkas, Foto dan Video di Android 13+
- Izin langsung diminta saat aplikasi pertama kali dibuka (dialog + `requestAllPermissions`)
- Opsi "Kelola Izin" di Pengaturan — buka halaman izin sistem aplikasi

### ♻️ Perubahan Fitur
- `PermissionHelper` — tambah `requestAllPermissions()` untuk minta semua izin (audio, video, images, notifikasi) sekaligus
- `PreferencesManager` — tambah `isFirstRun()` / `setFirstRunDone()` untuk deteksi pertama buka
- `MainActivity` — deteksi first run, tampil dialog izin, panggil `requestAllPermissions()`

### 🔧 Optimasi & Penyesuaian
- Hapus `aapt2FromMavenOverride` di `gradle.properties` — fix CI build (aapt2 tidak ditemukan)
- Tambah step decode keystore & buat `keystore.properties` di GitHub Actions — APK release sekarang tersign
- Tambah `REQUEST_IGNORE_BATTERY_OPTIMIZATIONS` — tombol "Hemat Baterai" di pengaturan

### ✏️ File Changed
- `AndroidManifest.xml` — tambah `READ_MEDIA_VIDEO`, `READ_MEDIA_IMAGES`, `REQUEST_IGNORE_BATTERY_OPTIMIZATIONS`
- `app/src/main/java/exp/miniplayer/utils/PermissionHelper.java` — tambah `requestAllPermissions()`
- `app/src/main/java/exp/miniplayer/utils/PreferencesManager.java` — tambah `isFirstRun()` / `setFirstRunDone()`
- `app/src/main/java/exp/miniplayer/MainActivity.java` — first run dialog & request permission
- `app/src/main/java/exp/miniplayer/ui/settings/SettingsFragment.java` — tambah tombol "Kelola Izin" & "Hemat Baterai"
- `app/src/main/res/layout/fragment_settings.xml` — tambah card Izin + baris Hemat Baterai
- `app/src/main/res/values/strings.xml` — tambah string baru
- `.github/workflows/release.yml` — tambah decode keystore + `keystore.properties`
- `gradle.properties` — hapus `aapt2FromMavenOverride`

### 🔢 Version
- versionCode: 19
- versionName: 1.1.0.10.1
---
## [1.1.0.10.0] - 2026-06-09

### ✨ Fitur Baru
- Statistik per item di perpustakaan — total lagu, total durasi, dan ukuran penyimpanan di Folder, Artis, Album, Playlist
- Header statistik di atas daftar — item count, total lagu, durasi, ukuran (Lagu, Artis, Album, Folder, Playlist, SongListActivity)
- Navigation Drawer — header total perpustakaan + jumlah item di setiap menu

### ♻️ Perubahan Fitur
- Daftar lagu Artis & Album: dari `AlertDialog` jadi `SongListActivity` dengan RecyclerView + album art
- `scanAllAudio()` filter by extension (.mp3/.m4a excluded) instead of IS_MUSIC flag — Audio Lainnya sekarang tampilkan .mp4, .mid, .midi, .amr, .3gp dll

### 🔧 Optimasi & Penyesuaian
- Setup signing config untuk release APK

### 🐞 Bug Fixes
- `ArtistsFragment` & `AlbumsFragment` — tambah `import exp.miniplayer.R` yang hilang
- `SongsFragment` — tambah `import android.widget.TextView` yang hilang
- `PlaylistFragment` — tambah `import android.widget.TextView` yang hilang
- `FoldersFragment` — tambah `import exp.miniplayer.utils.TimeUtils` yang hilang
- `AudioRepository.getFavoritesSync()` — ganti dari `.getValue()` ke query sync langsung

### 🗒️ File Added
- `keystore.properties` — konfigurasi signing release

### ✏️ File Changed
- `app/src/main/AndroidManifest.xml` — daftarkan SongListActivity
- `app/src/main/java/exp/miniplayer/model/Audio.java` — tambah field `fileSize`
- `app/src/main/java/exp/miniplayer/utils/MusicScanner.java` — simpan `SIZE` ke Audio; `scanAllAudio()` filter ekstensi bukan IS_MUSIC
- `app/src/main/java/exp/miniplayer/adapter/GroupAdapter.java` — `GroupItem` + `totalDuration`, `totalSize`, `formatSize()`
- `app/src/main/java/exp/miniplayer/adapter/PlaylistAdapter.java` — tambah `playlistStats` map, tampilkan stats
- `app/src/main/java/exp/miniplayer/database/FavoriteDao.java` — tambah `getAllFavoritesSync()`
- `app/src/main/java/exp/miniplayer/data/AudioRepository.java` — `getFavoritesSync()` pake query sync
- `app/src/main/java/exp/miniplayer/ui/folders/FoldersFragment.java` — stats per folder + header total + import TimeUtils
- `app/src/main/java/exp/miniplayer/ui/artists/ArtistsFragment.java` — ganti dialog ke intent + stats per artis + header total
- `app/src/main/java/exp/miniplayer/ui/albums/AlbumsFragment.java` — ganti dialog ke intent + stats per album + header total
- `app/src/main/java/exp/miniplayer/ui/playlist/PlaylistFragment.java` — stats per playlist + header total + import TextView
- `app/src/main/java/exp/miniplayer/ui/songs/SongsFragment.java` — header total + import TextView
- `app/src/main/java/exp/miniplayer/ui/songs/SongListActivity.java` — header total
- `app/src/main/java/exp/miniplayer/MainActivity.java` — `updateNavHeader()` header drawer + item counts + padding status bar
- `app/src/main/res/layout/item_group.xml` — tambah `group_stats`
- `app/src/main/res/layout/item_playlist.xml` — tambah `playlist_stats`
- `app/src/main/res/layout/fragment_folders.xml` — tambah `section_header` + FrameLayout wrapper
- `app/src/main/res/layout/fragment_artists.xml` — tambah `section_header` + FrameLayout wrapper
- `app/src/main/res/layout/fragment_albums.xml` — tambah `section_header` + FrameLayout wrapper
- `app/src/main/res/layout/fragment_playlist.xml` — tambah `section_header`
- `app/src/main/res/layout/fragment_songs.xml` — tambah `section_header`
- `app/src/main/res/layout/activity_song_list.xml` — tambah `section_header`
- `app/src/main/res/layout/nav_header.xml` — header drawer: background `colorPrimaryContainer`, icon app, nama, stats
- `app/src/main/res/layout/activity_main.xml` — tambah `headerLayout` di NavigationView; hapus `fitsSystemWindows`, pake padding manual biar gak mentok status bar
- `app/src/main/res/values/strings.xml` — tambah `folder_stats`, `section_stats`, `nav_header_stats`, `nav_item_count`
- `app/build.gradle` — default storeFile ke keystore baru
- `.gitignore` — ignore `*.jks` dan `keystore.properties`

### 🔥 File Removed
- Keystore lama — tidak terpakai

### 🔢 Version
- versionCode: 18
- versionName: 1.1.0.10.0
---
## [1.1.0.9.0] - 2026-06-09

### ✨ Fitur Baru
- Real-time auto-scan .m3u — `PlaylistFileWatcher` pakai `FileObserver`:
  playlist baru terdeteksi langsung tanpa restart aplikasi

### 🐞 Bug Fixes
- PlaylistAdapter selalu menampilkan "Playlist ini kosong" — tambah dukungan song count
- Auto-scan .m3u: judul lagu "Unknown" saat file path tidak match scannedAudio — fallback ke nama file + normalize path biar matching akurat
- Auto-scan .m3u: playlist lama yang sudah ada di DB dilewati — sekarang update title/artist lagu di tempat tanpa delete & re-insert

### ♻️ Perubahan Fitur
- `PlaylistScanner.importPlaylistFile()`: ganti deleteAll+insertAll jadi update in-place pake `REPLACE` by ID
- Toolbar header sekarang menampilkan judul perpustakaan yang aktif (Lagu, Playlist, Pengaturan, dll)
  bukan hanya judul aplikasi

### ✏️ File Changed
- `app/src/main/java/exp/miniplayer/adapter/PlaylistAdapter.java` — tampilkan jumlah lagu nyata dari `getPlaylistSongCount()`
- `app/src/main/java/exp/miniplayer/utils/PlaylistScanner.java` — fallback title pakai nama file, normalize path pake `getCanonicalPath()`, update in-place untuk playlist existing
- `app/src/main/java/exp/miniplayer/utils/PlaylistFileWatcher.java` — FileObserver real-time untuk file .m3u baru
- `app/src/main/java/exp/miniplayer/MiniPlayerApp.java` — inisialisasi PlaylistFileWatcher saat app start
- `app/src/main/java/exp/miniplayer/ui/playlist/PlaylistFragment.java` — kirim songCounts ke adapter
- `app/src/main/res/values/strings.xml` — tambah plural `playlist_song_count`
- `app/src/main/java/exp/miniplayer/MainActivity.java` — set toolbar title dari menu item terpilih

### 🔢 Version
- versionCode: 17
- versionName: 1.1.0.9.0
---
## [1.1.0.8.0] - 2026-06-09

### ✨ Fitur Baru
- Info teknis audio (bitrate, sample rate, codec) ditampilkan di mini player
- Format audio berwarna (hijau/oranye/merah) berdasarkan playability ExoPlayer
- Section collapsible ⋁/⋀ di tiap grup format (Audio, Rekaman, MIDI, Video, Stream)
- `FormatAudioActivity` — Activity khusus untuk pengaturan format audio
- `FolderSettingsActivity` — Activity untuk mengelola folder yang diizinkan/dikecualikan
- Export playlist (M3U per playlist, JSON semua playlist) via SAF
- Import playlist (M3U & JSON) via SAF — long-press FAB di daftar playlist
- Auto-scan file .m3u/.m3u8 dari folder musik — playlist langsung terdeteksi tanpa import manual

### ♻️ Perubahan Fitur
- Dialog "Format Audio" diganti dengan `FormatAudioActivity`
- Dialog "Folder Yang Diizinkan" dan "Folder Dikecualikan" diganti dengan `FolderSettingsActivity`

### 🔧 Optimasi & Penyesuaian
- Urutan section changelog distandarisasi di semua entry CHANGELOG.md
- Klasifikasi ulang entry antara ♻️ Perubahan Fitur dan 🔧 Optimasi & Penyesuaian
- Section non-standar (🎨 UX, 📚 Dokumentasi) digabung ke section standar
- AGENTS.md diperbarui — daftar urutan section changelog eksplisit
- README.md / README.txt — section changelog table diurutkan ulang

### 🐞 Bug Fixes
- Search di SongsFragment tidak berfungsi — import `SearchView` pakai kelas platform (`android.widget.SearchView`) tidak cocok dengan instance `androidx.appcompat.widget.SearchView` dari menu XML
- Mini player menutupi item terbawah di semua daftar — tambah `paddingBottom` pd RecyclerView sebesar mini player peek height
- Mini player menutupi opsi "Lihat Dokumentasi" di Settings — tambah `paddingBottom` di ScrollView fragment_settings

### 🗒️ File Added
- `app/src/main/res/layout/item_format_header.xml` — header section dengan ⋁/⋀
- `app/src/main/res/layout/item_format_checkbox.xml` — checkbox format
- `app/src/main/res/layout/activity_format_audio.xml` — layout FormatAudioActivity
- `app/src/main/res/layout/activity_folder_settings.xml` — layout FolderSettingsActivity
- `app/src/main/java/exp/miniplayer/ui/audioformat/FormatAudioActivity.java`
- `app/src/main/java/exp/miniplayer/ui/folders/FolderSettingsActivity.java`
- `app/src/main/java/exp/miniplayer/utils/PlaylistIO.java` — export/import M3U + JSON
- `app/src/main/java/exp/miniplayer/utils/PlaylistScanner.java` — auto-scan .m3u/.m3u8 dari folder musik

### ✏️ File Changed
- `app/src/main/java/exp/miniplayer/ui/songs/SongsFragment.java` — import SearchView diganti ke `androidx.appcompat.widget.SearchView`
- `app/build.gradle` — compileOptions Java 8 → 17 (tekan warning JDK 21)
- `app/src/main/res/values/dimens.xml` — tambah `mini_player_peek` (80dp)
- `app/src/main/res/layout/fragment_songs.xml` — paddingBottom mini_player_peek
- `app/src/main/res/layout/fragment_albums.xml` — paddingBottom mini_player_peek
- `app/src/main/res/layout/fragment_artists.xml` — paddingBottom mini_player_peek
- `app/src/main/res/layout/fragment_favorites.xml` — paddingBottom mini_player_peek
- `app/src/main/res/layout/fragment_playlist.xml` — paddingBottom mini_player_peek
- `app/src/main/res/layout/fragment_folders.xml` — paddingBottom mini_player_peek
- `app/src/main/res/layout/fragment_other_audio.xml` — paddingBottom mini_player_peek
- `app/src/main/res/layout/view_now_playing_sheet.xml` — tambah `mini_player_info` di mini player
- `app/src/main/java/exp/miniplayer/MainActivity.java` — update miniPlayerInfo dari metadata
- `app/src/main/res/layout/fragment_settings.xml` — paddingBottom mini_player_peek
- `app/src/main/res/values/colors.xml` — tambah `orange`, `red`
- `app/src/main/res/values/strings.xml` — string export/import
- `app/src/main/java/exp/miniplayer/ui/settings/SettingsViewModel.java` — method `getFormatPlayability()`, constants `PLAYABLE/MAYBE/NOT_PLAYABLE`
- `app/src/main/java/exp/miniplayer/ui/settings/SettingsFragment.java` — ganti dialog ke Activity, hapus kode dialog yang tidak dipakai
- `app/src/main/res/layout/dialog_audio_formats.xml` — redesign layout
- `app/src/main/AndroidManifest.xml` — daftarkan FormatAudioActivity & FolderSettingsActivity
- `app/src/main/java/exp/miniplayer/database/PlaylistDao.java` — tambah `getAllPlaylistsSync()`, `findByName()`
- `app/src/main/java/exp/miniplayer/data/AudioRepository.java` — method sync + getPlaylistSongDao + findPlaylistByName + integrasi PlaylistScanner
- `app/src/main/java/exp/miniplayer/ui/playlist/PlaylistFragment.java` — import playlist lewat long-press FAB
- `app/src/main/java/exp/miniplayer/ui/playlist/PlaylistDetailActivity.java` — export playlist via menu toolbar

### 🔢 Version
- versionCode: 16
- versionName: 1.1.0.8.0
---

### 🐞 Bug Fixes
- CHANGELOG.md dobel entry — hapus body `1.1.0.7.0` yang terlanjur tertinggal saat merge entry

### 💡 Catatan
- Tidak perlu di-release terpisah; cukup amend/fix pada rilis 1.1.0.7.0 yang sudah ada

### ✏️ File Changed
- `app/build.gradle` — version bump
- `CHANGELOG.md` — hapus duplikasi entry 1.1.0.7.0

### 🔢 Version
- versionCode: 14
- versionName: 1.1.0.7.2
---
## [1.1.0.7.0] - 2026-06-08

### ✨ Fitur Baru
- **Batasi Folder** — Pengaturan penyimpanan dirombak: toggle "Pindai Semua Audio" dan "Folder Disertakan" digabung menjadi satu toggle "Batasi Folder"
- **Lihat Folder Audio** — Opsi baru di Pengaturan untuk melihat semua folder yang berisi file audio
- **FolderListActivity** — Activity baru dengan dua mode tampilan: Kontrol (dikelompokkan per status Diizinkan/Dikecualikan/Belum Ditentukan, bisa dibuka-tutup) dan Semua (daftar flat dengan tebal/coret/biasa)
- **Drag antar Section** — Folder di mode Kontrol bisa di-long-press lalu drag ke section lain (Diizinkan/Dikecualikan/Belum Ditentukan); perubahan langsung tersimpan dan tersinkronisasi dengan pengaturan Folder

### ♻️ Perubahan Fitur
- Default audio format diubah — hanya mp3 dan m4a aktif secara default, format lain diaktifkan manual
- **Restrukturisasi Settings Storage** — "Folder yang Diizinkan" dipisah menjadi item terpisah dari "Batasi Pemindaian"; urutan: Batasi Pemindaian → Folder Diizinkan → Folder Dikecualikan → Lihat Folder Audio → Format Audio
- **Rename string** — "Batasi Folder" → "Batasi Pemindaian"; subtitle baru untuk setiap item folder (included/excluded)
- **Folder Diizinkan redup** — Saat Batasi Pemindaian off, "Folder yang Diizinkan" dan subtitlenya diredupkan (alpha 0.5)
- **excludedFolders selalu diterapkan** — `MusicScanner.scanAudio()` membaca excludedFolders tanpa tergantung `limitFolders` toggle
- Padding item folder diperbesar (8dp → 16dp vertical)
- Teks folder diperbesar (bodyMedium → bodyLarge)
- Elevasi saat drag: `setTranslationZ(16f)` saat drag start, `setTranslationZ(0f)` saat selesai

### 💡 Catatan
- Deskripsi semua file dilengkapi di STRUKTUR.md dan STRUKTUR.txt (anim, drawable, layout, menu, mipmap, values, xml, root files, assets, manifes, dsb.)

### ✏️ File Changed
- `app/build.gradle` — version bump
- `app/src/main/java/exp/miniplayer/utils/PreferencesManager.java` — DEFAULT_AUDIO_FORMATS, +limit_folders
- `app/src/main/java/exp/miniplayer/utils/MusicScanner.java` — scanAudio pakai isLimitFolders
- `app/src/main/java/exp/miniplayer/ui/settings/SettingsViewModel.java` — scanAllAudio → limitFolders
- `app/src/main/java/exp/miniplayer/ui/settings/SettingsFragment.java` — UI Batasi Folder + Lihat Folder Audio
- `app/src/main/res/layout/fragment_settings.xml` — layout storage dirombak
- `app/src/main/res/layout/activity_folder_list.xml` — layout baru
- `app/src/main/res/layout/item_folder_list.xml` — layout baru
- `app/src/main/res/values/strings.xml` — +diizinkan, belum_ditentukan, kontrol, semua; rename "Batasi Folder" → "Batasi Pemindaian"; +subtitle baru
- `app/src/main/AndroidManifest.xml` — +FolderListActivity
- `app/src/main/java/exp/miniplayer/ui/folders/FolderListActivity.java` — file baru, +ItemTouchHelper drag antar section, +setTranslationZ drag elevation
- `app/src/main/res/values/colors.xml` — +green
- `PANDUAN.md` — sinkron deskripsi fitur folder
- `app/src/main/assets/PANDUAN.txt` — sinkron deskripsi fitur folder
- `STRUKTUR.md` — deskripsi lengkap semua file
- `app/src/main/assets/STRUKTUR.txt` — deskripsi lengkap semua file

### 🔢 Version
- versionCode: 13
- versionName: 1.1.0.7.0
---
## [1.1.0.6.1] - 2026-06-08

### ♻️ Perubahan Fitur
- Default format audio aktif diubah — hanya mp3 dan m4a secara default, format lain harus diaktifkan manual di Pengaturan

### ✏️ File Changed
- `app/src/main/java/exp/miniplayer/utils/PreferencesManager.java` — DEFAULT_AUDIO_FORMATS hanya mp3, m4a

### 🔢 Version
- versionCode: 11
- versionName: 1.1.0.6.1
---
## [1.1.0.6.0] - 2026-06-07

### ✨ Fitur Baru
- MIDI playback support — putar file .mid/.midi/.rmi/.kar via ExoPlayer MidiRenderer + JSyn synthesizer

### ♻️ Perubahan Fitur
- Format MIDI (mid, midi, rmi, kar) aktif secara default di scanner audio

### ✏️ File Changed
- `build.gradle` — tambah JitPack repository untuk JSyn dependency
- `app/build.gradle` — version bump, tambah media3-exoplayer-midi
- `app/src/main/java/exp/miniplayer/player/MusicPlayer.java` — set extensionRendererMode PREFER
- `app/src/main/java/exp/miniplayer/utils/PreferencesManager.java` — tambah mid, midi, rmi, kar ke DEFAULT_AUDIO_FORMATS

### 🔢 Version
- versionCode: 10
- versionName: 1.1.0.6.0
---
## [1.1.0.5.1] - 2026-06-07

### 🔧 Optimasi & Penyesuaian
- Release workflow: release notes sekarang pakai CHANGELOG bukan auto-generate dari commit

### ✏️ File Changed
- `.github/workflows/release.yml` — ganti `generate_release_notes` dengan ekstraksi section user-facing dari CHANGELOG.md
- `app/build.gradle` — version bump

### 🔢 Version
- versionCode: 9
- versionName: 1.1.0.5.1
---
## [1.1.0.5.0] - 2026-06-07

### ✨ Fitur Baru
- Now Playing info strip — tampilkan sample rate, bitrate, dan format codec di expanded player
- Three-dot menu di Now Playing — Tambah ke Playlist, Rincian, Bagikan, Hapus
- Pilih Ikon Aplikasi — 5 ikon alternatif dari Pengaturan → Tampilan → Ikon Aplikasi

### 🗒️ File Added
- `app/src/main/res/drawable/ic_overflow.xml`
- `app/src/main/res/menu/now_playing_options_menu.xml`
- `app/src/main/res/drawable/ic_bg_purple.xml`
- `app/src/main/res/drawable/ic_bg_surface.xml`
- `app/src/main/res/drawable/ic_fg_note.xml`
- `app/src/main/res/drawable/ic_fg_play.xml`
- `app/src/main/res/drawable/ic_fg_queue.xml`
- `app/src/main/res/drawable/ic_fg_note_dark.xml`
- `app/src/main/res/drawable/ic_fg_play_dark.xml`
- 10 file `app/src/main/res/mipmap-anydpi-v26/ic_launcher_*.xml` (5 ikon × normal + round)

### ✏️ File Changed
- `app/build.gradle` — version bump
- `app/src/main/res/layout/view_now_playing_sheet.xml` — tambah TextView info strip + overflow button
- `app/src/main/java/exp/miniplayer/MainActivity.java` — info strip metadata, PopupMenu overflow, 4 handler (playlist, details, share, delete)
- `app/src/main/res/values/strings.xml` — tambah string untuk rincian dan menu
- `app/src/main/AndroidManifest.xml` — 5 activity-alias untuk pemilih ikon
- `app/src/main/res/layout/fragment_settings.xml` — tambah section Tampilan dengan row Ikon Aplikasi
- `app/src/main/java/exp/miniplayer/ui/settings/SettingsFragment.java` — tambah handler showIconDialog()

### 🔢 Version
- versionCode: 8
- versionName: 1.1.0.5.0
---
## [1.1.0.4.0] - 2026-06-06

### ✨ Fitur Baru
- DocumentationActivity — tampilan daftar dokumentasi, tap untuk baca isi dari assets (.txt)
- In-app Documentation Viewer — lihat dokumentasi langsung dari dalam aplikasi via Pengaturan

### ♻️ Perubahan Fitur
- README dan DEVELOPMENT digabung menjadi satu file — mengurangi jumlah file dokumentasi
- TENTANG diformat ulang mengikuti gaya FTxT (Lisensi & Klarifikasi, 👨‍💻 Author, 📧 Support)
- README.txt diubah dari format markdown ke plain text (underline), sama seperti PANDUAN.txt dan STRUKTUR.txt
- Tambah `**Current Release**` dan `**Last Updated**` di README
- Sinkronisasi emoji di CHANGELOG.txt — semua section headers pakai emoji

### 🗒️ File Added
- `release.sh` — Script GitHub release dengan changelog otomatis
- `TENTANG.md` — diformat ulang mengikuti gaya FTxT
- `app/src/main/java/exp/miniplayer/ui/documentation/DocumentationActivity.java`
- `app/src/main/res/layout/activity_documentation.xml`
- `app/src/main/assets/README.txt`
- `app/src/main/assets/PANDUAN.txt`
- `app/src/main/assets/STRUKTUR.txt`
- `app/src/main/assets/CHANGELOG.txt`

### ✏️ File Changed
- `README.md` — digabung dengan DEVELOPMENT, tambah Current Release & Last Updated, tambah Tentang
- `README.txt` — dari markdown ke plain text underline
- `AGENTS.md` — hapus referensi DEVELOPMENT/TENTANG
- `STRUKTUR.md`
- `app/src/main/assets/CHANGELOG.txt` — tambah emoji di section headers
- `app/src/main/assets/STRUKTUR.txt`
- `app/src/main/res/layout/fragment_settings.xml` — tambah section Dokumentasi
- `app/src/main/java/exp/miniplayer/ui/settings/SettingsFragment.java` — tambah handler Lihat Dokumentasi
- `app/src/main/AndroidManifest.xml` — daftarkan DocumentationActivity
- `app/src/main/res/values/strings.xml` — tambah string dokumentasi
- `app/src/main/java/exp/miniplayer/ui/documentation/DocumentationActivity.java` — hapus TENTANG.txt dari daftar

### 🔥 File Removed
- `DEVELOPMENT.md` — konten digabung ke README
- `app/src/main/assets/TENTANG.txt` — konten sudah di-merge ke README

### 🔢 Version
- versionCode: 7
- versionName: 1.1.0.4.0
---
## [1.1.0.3.0] - 2026-06-06

### ✨ Fitur Baru
- Format Audio di Settings — filter file berdasarkan ekstensi format (Audio, Rekaman, MIDI, Video, Stream)

### 🚮 Fitur Dihapus
- Opsi "Mode Ulang Default" di Pengaturan

### ♻️ Perubahan Fitur
- mini player selalu tampil di bawah meski tidak ada lagu diputar
- tampilan kosong: judul "Mini Player", subjudul "Pilih lagu untuk diputar"
- Scan All Audio diubah dari tombol klik jadi SwitchCompat — saat ON, semua audio dipindai tanpa filter folder; saat OFF, hanya folder yang disertakan dipindai
- Folder Disertakan otomatis dinonaktifkan (disabled/greyed out) saat Scan All Audio aktif
- Jaga Layar Tetap Nyala ditambahi informasi "Membatalkan batas waktu layar"
- Tambah opsi Putar Audio Diatas Aplikasi Lain — toggle + informasi "Tidak ada gangguan pemutaran"; implementasi: skip audio focus request saat ON, abandon fokus, pause kalah fokus saat OFF
- Tambah tombol panah bawah (▼) di Now Playing untuk menutup expanded player
- Icon favorit dirapikan — posisi, padding, hapus redundant android:tint
- Navigasi ganti lagu: dari ketukan sisi (previous/next) jadi swipe kiri/kanan di album art
- Tombol panah bawah & favorit di Now Playing tidak bisa ditekan — diperbaiki (mini_player_overlay disembunyikan saat expanded)

### 🐞 Bug Fixes
- lagu berhenti saat keluar app — `startForeground()` dipanggil di `playQueue()` SEBELUM player mulai (hindari `ForegroundServiceStartNotAllowedException` di Android 12+)
- lagu berhenti sendiri & now playing kosong (service restart) — simpan track terakhir ke SharedPreferences, pulihkan saat service restart
- file audio corrupt/codec error — ExoPlayer skip ke track berikutnya (onPlayerError)
- queue habis (repeat off) — togglePlayPause restart dari awal, bukan diam

### 🗒️ File Added
- `app/src/main/res/layout/dialog_audio_formats.xml`

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

### 🔢 Version
- versionCode: 6
- versionName: 1.1.0.3.0
---
## [1.0.0.2.0] - 2026-06-06

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
- folder picker kini pakai SAF (file manager) bukan input teks manual

### 🔧 Optimasi & Penyesuaian
- tambah izin WAKE_LOCK

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

### 🔢 Version
- versionCode: 5
- versionName: 1.0.0.2.0
---
## [1.0.0.1.0] - 2026-06-06

### ✨ Fitur Baru
- migrasi navigasi dari Bottom Navigation Bar ke Navigation Drawer (sidebar)
- tambah Toolbar dengan hamburger icon

### ♻️ Perubahan Fitur
- warna diubah ke palet dark theme Material3 sepenuhnya

### 🐞 Bug Fixes
- fix force close SongsFragment — cast SearchView pakai instanceof

### ✏️ File Changed
- `MainActivity.java`
- `SongsFragment.java`
- `activity_main.xml`
- `strings.xml`
- `app/src/main/res/values/colors.xml` — +green
- `STRUKTUR.md`

### 🔥 File Removed
- `app/src/main/res/values-night/` — tidak terpakai setelah migrasi ke dark theme sepenuhnya

### 🔢 Version
- versionCode: 4
- versionName: 1.0.0.1.0
---
## [1.0.0.0.2] - 2026-06-06

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

### 🔢 Version
- versionCode: 3
- versionName: 1.0.0.0.2
---
## [1.0.0.0.1] - 2026-06-06

### 🔧 Optimasi & Penyesuaian
- hapus `aapt2FromMavenOverride` dari gradle.properties untuk kompatibilitas CI

### ✏️ File Changed
- `gradle.properties`
- `app/build.gradle`

### 🔢 Version
- versionCode: 2
- versionName: 1.0.0.0.1
---
## [1.0.0.0.0] - 2026-06-06

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

### 🔢 Version
- versionCode: 1
- versionName: 1.0.0.0.0
---