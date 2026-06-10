MINI PLAYER
===========

Current Release: 1.1.0.11.6
Last Updated: 2026-06-10

Aplikasi pemutar musik lokal untuk Android dengan fitur lengkap dan antarmuka minimalis.

---

FITUR UTAMA
-----------
- Pemutaran Musik Lokal - Putar file audio dari penyimpanan perangkat
- MIDI Playback - Putar file MIDI (.mid/.midi/.rmi/.kar) dengan dukungan synthesizer
- Daftar Lagu - Lihat semua lagu yang terdeteksi dengan informasi detail
- Favorit - Tandai lagu favorit untuk akses cepat
- Playlist - Buat, kelola, dan atur playlist kustom
- Now Playing Bottom Sheet - Pemutaran penuh dalam Bottom Sheet (mini player + layar penuh)
- Mode Acak & Ulang - Shuffle, Repeat All, Repeat One
- Dark Mode - Tema gelap selalu aktif
- Pencarian - Cari lagu dengan cepat
- Background Playback - Musik tetap berjalan saat aplikasi di-minimize
- Notifikasi - Kontrol pemutaran dari notifikasi
- Kustomisasi Ikon - 5 pilihan ikon aplikasi dari Pengaturan
- Format Audio - Filter ekstensi file yang dipindai dengan indikator playability (hijau/oranye/merah)
- Batasi Pemindaian - Filter folder audio yang dipindai dengan folder diizinkan/dikecualikan
- Lihat Folder Audio - Lihat semua folder audio dengan status (Diizinkan/Dikecualikan/Belum Ditentukan)
- Export/Import Playlist - Ekspor playlist ke .m3u atau .json via SAF; impor dari file .m3u/.json
- Auto-scan Playlist - File .m3u/.m3u8 di folder musik otomatis terdeteksi sebagai playlist
- Info Teknis Audio - Lihat bitrate, sample rate, dan codec di Now Playing
- Dokumentasi In-App - Baca dokumentasi langsung dari dalam aplikasi

---

DOKUMENTASI TERKAIT
-------------------
File              Isi
PANDUAN.txt       Panduan penggunaan lengkap
CHANGELOG.txt     Riwayat perubahan lengkap

Dokumentasi juga tersedia di dalam aplikasi melalui Pengaturan.

---

LISENSI & KLARIFIKASI
---------------------
Belum ada lisensi resmi yang ditetapkan untuk project ini.

Sebagian besar pengembangan dibantu AI, sementara pengembang menangani pengujian, penyesuaian implementasi, revisi, dan debugging sambil ngopi.

Silakan gunakan, modifikasi, fork, atau kustomisasi sesuai kebutuhan.

---

AUTHOR
------
Developed by GaoZhan.

Aplikasi pemutar musik Android Mini Player dengan fokus pada pemutaran audio lokal, manajemen playlist, dan antarmuka minimalis.

---

SUPPORT
-------
Laporan bug, issue, atau permintaan fitur:
Silakan buat issue di repository project atau hubungi pengembang.

Respons tidak dijamin cepat, karena project ini berkembang mengikuti eksperimen, suasana hati, waktu luang, dan secangkir kopi.

---

DEVELOPMENT
-----------

ENVIRONMENT
-----------
Item               Detail
Build System       Gradle + AGP 8.12.0
Java               Java 17 (source/target)
Min SDK            26
Target SDK         35
Compile SDK        35
Namespace          exp.miniplayer
Application ID     exp.miniplayer

VERSIONING
----------
Project ini TIDAK menggunakan Semantic Versioning standar. Format khusus: major.removed.restored.minor.patch

Komponen  Arti
major     Milestone besar / arsitektur
removed   Counter fitur yang dihapus (tidak turun)
restored  Counter fitur yang dipulihkan (tidak turun)
minor     Feature release counter
patch     Bugfix / optimization / maintenance

Aturan:
- patch reset ke 0 saat minor naik
- minor +1 saat major, removed, atau restored naik

SECTION CHANGELOG
-----------------
Section                    Deskripsi
✨ Fitur Baru              Fitur baru ditambahkan
🚮️ Fitur Dihapus           Fitur dihapus/dinonaktifkan
📥️ Fitur Dipulihkan       Fitur lama dikembalikan
♻️️ Perubahan Fitur        Perubahan fitur existing
🔧 Optimasi & Penyesuaian Optimasi, refactor, maintenance
🐞 Bug Fixes              Perbaikan bug
💡 Catatan                Informasi tambahan
🗒️ File Added             File baru
✏️️ File Changed           File diubah
🔥️ File Removed           File dihapus
🔢 Version                versionCode & versionName

DEPENDENCIES
------------
Library                          Version  Fungsi
AndroidX AppCompat               1.7.0    UI compatibility
Material Design                  1.12.0   Material 3 components
ConstraintLayout                 2.2.0    Layout
RecyclerView                     1.3.2    Daftar lagu/playlist
CardView                         1.0.0    Card containers
Lifecycle ViewModel              2.8.7    MVVM ViewModel
Lifecycle LiveData               2.8.7    Reactive data
Lifecycle Runtime                2.8.7    Lifecycle handling
Media3 ExoPlayer                 1.5.1    Audio playback
Media3 Session                   1.5.1    Media session
Room Runtime                     2.6.1    Local database
Room Compiler                    2.6.1    Room annotation processor
AndroidX Core                    1.15.0   Core utilities
AndroidX Fragment                1.8.5    Fragment management
AndroidX Media                   1.7.0    Media session compat

ARCHITECTURE
------------
MVVM (Model-View-ViewModel):

- Model - Room database entities, DAO, Repository
- View - Activity, Fragment, ViewBinding
- ViewModel - ViewModel + LiveData

BUILD
-----
# Debug build
./gradlew assembleDebug

# Release build
./gradlew assembleRelease

PERMISSION
----------
Dideklarasikan di AndroidManifest.xml:

- READ_EXTERNAL_STORAGE (maxSdk 32) - Akses penyimpanan Android <=12
- READ_MEDIA_AUDIO - Akses file audio (API 33+)
- READ_MEDIA_VIDEO - Akses file video (API 33+)
- READ_MEDIA_IMAGES - Akses file gambar (API 33+)
- FOREGROUND_SERVICE - Layanan latar depan
- FOREGROUND_SERVICE_MEDIA_PLAYBACK - Layanan pemutaran media
- POST_NOTIFICATIONS - Notifikasi pemutaran (API 33+)
- BLUETOOTH (maxSdk 31) - Bluetooth klasik
- BLUETOOTH_CONNECT - Kontrol Bluetooth AVRCP (API 31+)
- WAKE_LOCK - Jaga CPU tetap aktif
- REQUEST_IGNORE_BATTERY_OPTIMIZATIONS - Nonaktifkan optimasi baterai

Izin diminta otomatis saat pertama aplikasi dibuka. Pengguna juga bisa
mengelola izin & nonaktifkan optimasi baterai melalui menu Pengaturan.

---

STRUKTUR PROJECT
-----------------

AudiPlay/
|
+-- .github/workflows/
|   +-- release.yml              - GitHub Actions CI: build APK & buat GitHub Release
|
+-- AGENTS.md                    - Pedoman AI agent
+-- CHANGELOG.md                 - Riwayat perubahan per release
+-- PANDUAN.md                   - Panduan penggunaan lengkap
+-- README.md                    - Ringkasan project & fitur
+-- build.gradle                 - Root Gradle: deklarasi plugin AGP, repository
+-- gradle.properties            - Gradle: AndroidX, Jetifier, JVM args
+-- gradlew / gradlew.bat        - Gradle wrapper scripts
+-- release.sh                   - Script GitHub release
+-- settings.gradle              - Settings Gradle (include :app)
+-- keystore.properties          - Konfigurasi signing release
|
+-- gradle/wrapper/
|   +-- gradle-wrapper.properties - Konfigurasi Gradle wrapper
|
+-- app/
    +-- build.gradle             - Module: minSdk 26, targetSdk 35, Room/Media3/Material3
    +-- proguard-rules.pro       - Aturan ProGuard (kosong, belum ada custom rules)
    |
    +-- src/main/
        +-- AndroidManifest.xml  - Permission, activity-alias ikon, 7 Activity, MusicService
        |
        +-- assets/
        |   +-- CHANGELOG.txt    - Riwayat perubahan (in-app)
        |   +-- othericon/       - 17 ikon launcher alternatif
        |   +-- PANDUAN.txt      - Panduan penggunaan (in-app)
        |   +-- README.txt       - Ringkasan project (in-app)
        |
        +-- java/exp/miniplayer/
        |   +-- MainActivity.java         - Activity utama: DrawerLayout, BottomSheet, service binding
        |   +-- MiniPlayerApp.java        - Application: dark mode, PlaylistFileWatcher
        |   |
        |   +-- adapter/
        |   |   +-- GroupAdapter.java          - RecyclerView grup album/artis
        |   |   +-- PlaylistAdapter.java       - RecyclerView daftar playlist
        |   |   +-- PlaylistDetailAdapter.java - RecyclerView lagu dalam playlist
        |   |   +-- SongAdapter.java           - RecyclerView item Audio
        |   |
        |   +-- data/
        |   |   +-- AudioRepository.java       - Jembatan Room DB, scanner, preferences
        |   |
        |   +-- database/
        |   |   +-- AppDatabase.java           - Room DB singleton
        |   |   +-- FavoriteDao.java           - DAO tabel favorites
        |   |   +-- FavoriteEntity.java        - Entity favorit
        |   |   +-- PlaylistDao.java           - DAO tabel playlists
        |   |   +-- PlaylistEntity.java        - Entity playlist
        |   |   +-- PlaylistSongDao.java       - DAO tabel playlist_songs
        |   |   +-- PlaylistSongEntity.java    - Entity playlist_songs
        |   |
        |   +-- model/
        |   |   +-- Audio.java                 - Model Parcelable track audio
        |   |
        |   +-- player/
        |   |   +-- MusicPlayer.java           - Wrapper Media3 ExoPlayer
        |   |
        |   +-- service/
        |   |   +-- MusicService.java          - Foreground service, notifikasi kontrol
        |   |
        |   +-- ui/
        |   |   +-- albums/AlbumsFragment.java             - Daftar album hasil scan
        |   |   +-- artists/ArtistsFragment.java           - Daftar artis hasil scan
        |   |   +-- audioformat/FormatAudioActivity.java   - Pengaturan format audio
        |   |   +-- documentation/DocumentationActivity.java - Baca file .txt dari assets
        |   |   +-- favorites/FavoritesFragment.java       - Daftar lagu favorit
        |   |   +-- favorites/FavoritesViewModel.java      - ViewModel data favorit
        |   |   +-- folders/FolderListActivity.java        - Kelola folder (Kontrol/Semua)
        |   |   +-- folders/FolderSettingsActivity.java    - Atur folder via SAF
        |   |   +-- folders/FoldersFragment.java           - Folder per direktori induk
        |   |   +-- other_audio/OtherAudioFragment.java    - File audio non-musik
        |   |   +-- playlist/PlaylistDetailActivity.java   - Daftar lagu dalam playlist
        |   |   +-- playlist/PlaylistDetailViewModel.java  - ViewModel detail playlist
        |   |   +-- playlist/PlaylistFragment.java         - Kelola playlist
        |   |   +-- playlist/PlaylistViewModel.java        - ViewModel daftar playlist
        |   |   +-- settings/SettingsFragment.java         - Pengaturan aplikasi
        |   |   +-- settings/SettingsViewModel.java        - ViewModel state pengaturan
        |   |   +-- songs/SongsFragment.java               - Semua lagu + search/sort
        |   |   +-- songs/SongsViewModel.java              - ViewModel daftar lagu
        |   |   +-- songs/SongListActivity.java            - Lagu dari artis/album/folder
        |   |   +-- system_picker/SystemPickerFragment.java- Impor audio via SAF
        |   |
        |   +-- utils/
        |       +-- MusicScanner.java          - Pindai MediaStore filter folder & format
        |       +-- PermissionHelper.java      - Handler izin runtime
        |       +-- PlaylistIO.java            - Export/import playlist M3U + JSON
        |       +-- PlaylistFileWatcher.java   - FileObserver real-time .m3u
        |       +-- PlaylistScanner.java       - Auto-scan .m3u/.m3u8
        |       +-- PreferencesManager.java    - Wrapper SharedPreferences
        |       +-- QueueHolder.java           - Singleton antrean pemutaran
        |       +-- TimeUtils.java             - Format milidetik ke durasi
        |
        +-- res/
            +-- anim/                     - Animasi transisi
            |   +-- slide_in_up.xml       - Slide up (300ms) now-playing sheet
            |   +-- slide_out_down.xml    - Slide down (300ms) now-playing sheet
            |   +-- stay.xml              - Animasi diam placeholder
            |
            +-- drawable/                 - Ikon & shape vector
            |   +-- drag_handle_background.xml   - Shape rounded rectangle drag handle
            |   +-- ic_album_default.xml         - CD/disc placeholder album art
            |   +-- ic_arrow_down.xml            - Chevron bawah tutup now-playing
            |   +-- ic_artist.xml                - Siluet orang navigasi Artists
            |   +-- ic_bg_purple.xml             - Rectangle ungu bg launcher
            |   +-- ic_bg_surface.xml            - Rectangle gelap bg launcher
            |   +-- ic_close.xml                 - Ikon X hapus folder
            |   +-- ic_favorite.xml              - Hati solid status favorit
            |   +-- ic_favorite_border.xml       - Hati outline tambah favorit
            |   +-- ic_fg_note.xml               - Nota musik putih fg launcher
            |   +-- ic_fg_note_dark.xml          - Nota musik ungu fg launcher
            |   +-- ic_fg_play.xml               - Segitiga play putih fg launcher
            |   +-- ic_fg_play_dark.xml          - Segitiga play ungu fg launcher
            |   +-- ic_fg_queue.xml              - Antrian putih fg launcher
            |   +-- ic_folder.xml                - Folder navigasi Folders
            |   +-- ic_launcher_background.xml   - Background ungu default launcher
            |   +-- ic_launcher_foreground.xml   - Nota musik putih detail
            |   +-- ic_music_note.xml            - Nota musik navigasi Songs
            |   +-- ic_other_audio.xml           - Nota + gelombang navigasi Other Audio
            |   +-- ic_overflow.xml              - Tiga titik vertikal overflow menu
            |   +-- ic_pause.xml                 - Dua garis vertikal pause
            |   +-- ic_play.xml                  - Segitiga kanan play
            |   +-- ic_playlist_add.xml          - Daftar + plus tambah playlist
            |   +-- ic_queue_music.xml           - Daftar + nota navigasi Playlists
            |   +-- ic_repeat.xml                - Panah melingkar repeat all
            |   +-- ic_repeat_one.xml            - Panah + angka 1 repeat one
            |   +-- ic_search.xml                - Kaca pembesar search
            |   +-- ic_settings.xml              - Gear navigasi Settings & sort
            |   +-- ic_shuffle.xml               - Panah silang shuffle
            |   +-- ic_skip_next.xml             - Dua panah kanan next track
            |   +-- ic_skip_previous.xml         - Dua panah kiri previous track
            |   +-- ic_system_picker.xml         - Monitor navigasi System Picker
            |
            +-- layout/                   - Layout XML
            |   +-- activity_documentation.xml   - Toolbar, card list, ScrollView monospace
            |   +-- activity_folder_list.xml     - Toolbar, toggle, RecyclerView
            |   +-- activity_folder_settings.xml - RecyclerView folder + FAB
            |   +-- activity_format_audio.xml    - RecyclerView grup + checkbox
            |   +-- activity_main.xml            - DrawerLayout + CoordinatorLayout
            |   +-- activity_playlist_detail.xml - Toolbar + RecyclerView lagu
            |   +-- activity_song_list.xml       - Toolbar + section_header
            |   +-- nav_header.xml               - Header Navigation Drawer
            |   +-- dialog_audio_formats.xml     - ScrollView + checkbox format
            |   +-- dialog_folder_input.xml      - EditText input folder
            |   +-- dialog_folder_list.xml       - Daftar folder + empty state
            |   +-- fragment_albums.xml          - section_header + RecyclerView
            |   +-- fragment_artists.xml         - section_header + RecyclerView
            |   +-- fragment_favorites.xml       - RecyclerView + empty state
            |   +-- fragment_folders.xml         - section_header + RecyclerView
            |   +-- fragment_other_audio.xml     - RecyclerView + empty state
            |   +-- fragment_playlist.xml        - ConstraintLayout + FAB
            |   +-- fragment_settings.xml        - ScrollView card settings
            |   +-- fragment_songs.xml           - ConstraintLayout + RecyclerView
            |   +-- fragment_system_picker.xml   - MaterialCardView tap target
            |   +-- item_folder.xml              - Text path + tombol close
            |   +-- item_folder_list.xml         - Path, file count, status
            |   +-- item_format_checkbox.xml     - Checkbox + indikator playability
            |   +-- item_format_header.xml       - Header grup collapsible
            |   +-- item_group.xml               - Group album/artis + stats
            |   +-- item_playlist.xml            - Ikon queue + nama + stats
            |   +-- item_song.xml                - Album art + judul + artis
            |   +-- view_now_playing_sheet.xml   - Cover + seekbar + kontrol
            |
            +-- menu/                     - Menu XML
            |   +-- bottom_nav_menu.xml          - Navigation drawer 8 item
            |   +-- now_playing_options_menu.xml - Overflow: playlist/rincian
            |   +-- song_options_menu.xml        - Context menu favorit/playlist
            |   +-- songs_menu.xml               - Toolbar SearchView/Sort
            |
            +-- mipmap-anydpi-v26/         - Adaptive icon launcher
            |   +-- ic_launcher.xml              - Default adaptive icon
            |   +-- ic_launcher_round.xml        - Default round adaptive
            |   +-- ic_launcher_note.xml         - "Nota Musik" adaptive
            |   +-- ic_launcher_note_round.xml
            |   +-- ic_launcher_note_dark.xml    - "Nota Musik (Gelap)"
            |   +-- ic_launcher_note_dark_round.xml
            |   +-- ic_launcher_play.xml         - "Putar" adaptive
            |   +-- ic_launcher_play_round.xml
            |   +-- ic_launcher_play_dark.xml    - "Putar (Gelap)"
            |   +-- ic_launcher_play_dark_round.xml
            |   +-- ic_launcher_queue.xml        - "Antrian" adaptive
            |   +-- ic_launcher_queue_round.xml
            |
            +-- values/                   - Resource values
            |   +-- colors.xml                  - Warna Material3 dark theme
            |   +-- dimens.xml                  - Dimensi layout
            |   +-- strings.xml                 - Semua string UI Bahasa Indonesia
            |   +-- themes.xml                  - Theme Material3 Dark NoActionBar
            |
            +-- xml/
                +-- backup_rules.xml        - Aturan backup SharedPreferences + Room DB

STATISTIK PROJECT
-----------------
Kategori                     Jumlah
Java source                  45
Layout XML                   28
Drawable XML                 32
Values XML                   4
Mipmap XML                   12
Menu XML                     4
Anim XML                     3
XML lainnya (Manifest,dll)   2
Assets (txt, png)            20
Root dokumen                 5
Root konfigurasi             5
Gradle & wrapper             6
CI/CD                        1
Total file                   167
Total direktori              58