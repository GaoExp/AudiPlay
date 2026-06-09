MINI PLAYER
===========

Current Release: 1.1.0.10.1
Last Updated: 2026-06-09

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
STRUKTUR.txt      Struktur project & deskripsi file
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