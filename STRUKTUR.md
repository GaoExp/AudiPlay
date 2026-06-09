# Struktur Project

```
AudiPlay/
│
├── .github/
│   └── workflows/
│       └── release.yml              — GitHub Actions CI: build APK & buat GitHub Release saat tag push v*
│
├── AGENTS.md                        — Pedoman AI agent (versioning, dokumentasi, workflow)
├── CHANGELOG.md                     — Riwayat perubahan per release (Markdown)
├── PANDUAN.md                       — Panduan penggunaan lengkap (Markdown)
├── README.md                        — Ringkasan project, fitur, development, lisensi (Markdown)
├── STRUKTUR.md                      — Struktur direktori & deskripsi file (Markdown)
├── build.gradle                     — Root Gradle: deklarasi plugin AGP 8.12.0, repository
├── gradle.properties                — Gradle properties: AndroidX, Jetifier, JVM args 2048m
├── gradlew                          — Gradle wrapper script (Unix/macOS)
├── gradlew.bat                      — Gradle wrapper script (Windows)
├── release.sh                       — Script GitHub release: ekstrak CHANGELOG, jalankan gh release create
├── settings.gradle                  — Settings Gradle: include module :app
│
├── gradle/
│   └── wrapper/
│       └── gradle-wrapper.properties — Konfigurasi Gradle wrapper (Gradle 8.13)
│
└── app/
    ├── build.gradle                 — Module app: applicationId exp.miniplayer, compileSdk 35, minSdk 26, ViewBinding, dependensi Room/Media3/Material3
    ├── proguard-rules.pro           — Aturan ProGuard (kosong, belum ada custom rules)
    ├── libs/                        — Direktori JAR libs (kosong)
    │
    └── src/main/
        ├── AndroidManifest.xml      — Manifest: permissions audio/notifikasi/BT, 5 activity-alias ikon, 6 Activity, MusicService
        │
        ├── assets/
        │   ├── CHANGELOG.txt        — Riwayat perubahan (plain text, untuk in-app viewer)
        │   ├── PANDUAN.txt          — Panduan penggunaan (plain text, untuk in-app viewer)
        │   ├── README.txt           — Ringkasan project (plain text, untuk in-app viewer)
        │   └── STRUKTUR.txt         — Struktur project (plain text, untuk in-app viewer)
        │
        ├── java/exp/miniplayer/
        │   ├── MainActivity.java            — Activity utama: DrawerLayout navigasi, BottomSheet now-playing, service binding, info strip, PopupMenu overflow
        │   ├── MiniPlayerApp.java            — Application subclass: memaksa mode gelap, inisialisasi PlaylistFileWatcher
        │   │
        │   ├── adapter/
        │   │   ├── GroupAdapter.java             — Adapter RecyclerView grup album/artis (nama + jumlah item)
        │   │   ├── PlaylistAdapter.java          — Adapter RecyclerView daftar playlist (nama + jumlah lagu)
        │   │   ├── PlaylistDetailAdapter.java    — Adapter RecyclerView lagu dalam playlist
        │   │   └── SongAdapter.java              — Adapter RecyclerView item Audio (album art, judul, artis, durasi)
        │   │
        │   ├── data/
        │   │   └── AudioRepository.java          — Repository: jembatan Room database, MusicScanner, PreferencesManager
        │   │
        │   ├── database/
        │   │   ├── AppDatabase.java              — Room database singleton (favorites, playlists, playlist_songs)
        │   │   ├── FavoriteDao.java              — Room DAO CRUD tabel favorites
        │   │   ├── FavoriteEntity.java           — Room entity tabel favorit (audioId, title, artist, album, dll)
        │   │   ├── PlaylistDao.java              — Room DAO CRUD tabel playlists
        │   │   ├── PlaylistEntity.java           — Room entity tabel playlists (id autoGenerate, name, createdAt)
        │   │   ├── PlaylistSongDao.java          — Room DAO CRUD tabel playlist_songs
        │   │   └── PlaylistSongEntity.java       — Room entity tabel playlist_songs (audioId, sortOrder)
        │   │
        │   ├── model/
        │   │   └── Audio.java                    — Model data Parcelable track audio (id, title, artist, album, durasi, uri, albumArt, dateAdded, filePath)
        │   │
        │   ├── player/
        │   │   └── MusicPlayer.java              — Wrapper Media3 ExoPlayer: queue, shuffle, repeat, progress, error handling, MIDI extension renderer
        │   │
        │   ├── service/
        │   │   └── MusicService.java             — Foreground service: audio focus, notifikasi kontrol playback, persistensi track terakhir
        │   │
        │   ├── ui/
        │   │   ├── albums/
        │   │   │   └── AlbumsFragment.java       — Fragment daftar album dari hasil scan (tap filter lagu per album)
        │   │   ├── artists/
        │   │   │   └── ArtistsFragment.java      — Fragment daftar artis dari hasil scan (tap filter lagu per artis)
        │   │   ├── audioformat/
        │   │   │   └── FormatAudioActivity.java  — Activity pengaturan format audio dengan grup collapsible & indikator warna playability
        │   │   ├── documentation/
        │   │   │   └── DocumentationActivity.java — Activity baca & tampilkan file .txt dari assets (README, PANDUAN, STRUKTUR, CHANGELOG)
        │   │   ├── favorites/
        │   │   │   ├── FavoritesFragment.java    — Fragment daftar lagu favorit (tap putar, popup menu hapus favorit)
        │   │   │   └── FavoritesViewModel.java   — ViewModel data favorit via LiveData dari repository
        │   │   ├── folders/
        │   │   │   ├── FolderListActivity.java   — Activity kelola folder audio: mode Kontrol (drag antar section Diizinkan/Dikecualikan/Belum Ditentukan) & mode Semua (daftar flat)
        │   │   │   ├── FolderSettingsActivity.java — Activity atur folder yang diizinkan & dikecualikan via SAF picker
        │   │   │   └── FoldersFragment.java      — Fragment folder musik per direktori induk (tap putar semua lagu dalam folder)
        │   │   ├── other_audio/
        │   │   │   └── OtherAudioFragment.java   — Fragment file audio non-musik (rekaman, dll)
        │   │   ├── playlist/
        │   │   │   ├── PlaylistDetailActivity.java  — Activity daftar lagu dalam playlist (tap putar, long-press hapus)
        │   │   │   ├── PlaylistDetailViewModel.java — ViewModel detail playlist via LiveData
        │   │   │   ├── PlaylistFragment.java        — Fragment kelola playlist (buat/rename/hapus via FAB & long-press)
        │   │   │   └── PlaylistViewModel.java       — ViewModel daftar playlist via LiveData
        │   │   ├── settings/
        │   │   │   ├── SettingsFragment.java     — Fragment pengaturan: screen-on, audio focus, batasi folder, format audio, ikon aplikasi, dokumentasi
        │   │   │   └── SettingsViewModel.java    — ViewModel state pengaturan & preferensi folder (limitFolders, audioFormats, dll)
        │   │   ├── songs/
        │   │   │   ├── SongsFragment.java        — Fragment semua lagu: search, sort, tap putar, long-press favorit/playlist
        │   │   │   └── SongsViewModel.java       — ViewModel daftar lagu dengan filter, sorting, scan-once logic
        │   │   └── system_picker/
        │   │       └── SystemPickerFragment.java — Fragment impor audio via system file picker (SAF)
        │   │
        │   └── utils/
        │       ├── MusicScanner.java             — Pindai MediaStore dengan filter folder (include/exclude) & filter format audio
        │       ├── PermissionHelper.java         — Handler izin runtime audio/storage & notifikasi
        │       ├── PlaylistIO.java               — Export/import playlist (M3U extended + JSON) via SAF
        │       ├── PlaylistFileWatcher.java      — FileObserver real-time: deteksi file .m3u baru dan trigger auto-scan
        │       ├── PlaylistScanner.java          — Auto-scan file .m3u/.m3u8 dari folder musik, parse & import/update ke Room DB
        │       ├── PreferencesManager.java       — Wrapper SharedPreferences: screen-on, audio-focus, folder filter, format audio, last track, sort mode
        │       ├── QueueHolder.java              — Singleton statis antrean pemutaran (list Audio + starting index)
        │       └── TimeUtils.java                — Format milidetik ke string durasi (1:23 / 1:02:05)
        │
        └── res/
            ├── anim/
            │   ├── slide_in_up.xml               — Animasi slide up (300ms, 100%p → 0%p) untuk now-playing sheet
            │   ├── slide_out_down.xml             — Animasi slide down (300ms, 0%p → 100%p) untuk now-playing sheet
            │   └── stay.xml                       — Animasi diam (300ms, 0%p) — placeholder
            │
            ├── drawable/
            │   ├── drag_handle_background.xml     — Shape rounded rectangle (2dp) untuk drag handle now-playing
            │   ├── ic_album_default.xml           — Ikon CD/disc default untuk placeholder album art
            │   ├── ic_arrow_down.xml              — Panah chevron bawah — tombol tutup now-playing
            │   ├── ic_artist.xml                  — Siluet orang — ikon navigasi Artists
            │   ├── ic_bg_purple.xml               — Rectangle ungu solid (108dp) — background ikon launcher tema ungu
            │   ├── ic_bg_surface.xml              — Rectangle gelap solid (108dp) — background ikon launcher tema gelap
            │   ├── ic_close.xml                   — Ikon X — tombol hapus folder di settings
            │   ├── ic_favorite.xml                — Hati solid — status favorit
            │   ├── ic_favorite_border.xml         — Hati outline — tombol tambah favorit
            │   ├── ic_fg_note.xml                 — Nota musik putih — foreground launcher "Nota Musik"
            │   ├── ic_fg_note_dark.xml            — Nota musik ungu — foreground launcher "Nota Musik (Gelap)"
            │   ├── ic_fg_play.xml                 — Segitiga play putih — foreground launcher "Putar"
            │   ├── ic_fg_play_dark.xml            — Segitiga play ungu — foreground launcher "Putar (Gelap)"
            │   ├── ic_fg_queue.xml                — Antrian putih — foreground launcher "Antrian"
            │   ├── ic_folder.xml                  — Ikon folder — ikon navigasi Folders
            │   ├── ic_launcher_background.xml     — Background ungu (108dp) — default launcher background
            │   ├── ic_launcher_foreground.xml     — Nota musik putih detail — default launcher foreground
            │   ├── ic_music_note.xml              — Nota musik — ikon navigasi Songs
            │   ├── ic_other_audio.xml             — Nota musik + gelombang — ikon navigasi Other Audio
            │   ├── ic_overflow.xml                — Tiga titik vertikal — tombol overflow menu now-playing
            │   ├── ic_pause.xml                   — Dua garis vertikal — tombol pause
            │   ├── ic_play.xml                    — Segitiga kanan — tombol play
            │   ├── ic_playlist_add.xml            — Daftar + plus — tombol tambah ke playlist
            │   ├── ic_queue_music.xml             — Daftar + nota musik — ikon navigasi Playlists
            │   ├── ic_repeat.xml                  — Panah melingkar — mode repeat all
            │   ├── ic_repeat_one.xml              — Panah melingkar + angka 1 — mode repeat one
            │   ├── ic_search.xml                  — Kaca pembesar — tombol search
            │   ├── ic_settings.xml                — Gear — ikon navigasi Settings & tombol sort
            │   ├── ic_shuffle.xml                 — Panah silang — mode shuffle
            │   ├── ic_skip_next.xml               — Dua panah kanan — tombol next track
            │   ├── ic_skip_previous.xml           — Dua panah kiri — tombol previous track
            │   └── ic_system_picker.xml           — Monitor/display — ikon navigasi System Picker
            │
            ├── layout/
            │   ├── activity_documentation.xml     — Layout daftar dokumentasi (toolbar, card list, ScrollView konten monospace)
            │   ├── activity_folder_list.xml       — Layout folder list (toolbar, toggle Kontrol/Semua, RecyclerView, ItemTouchHelper)
            │   ├── activity_folder_settings.xml   — Layout atur folder diizinkan/dikecualikan (RecyclerView + FAB)
            │   ├── activity_format_audio.xml      — Layout pengaturan format audio (RecyclerView grup + checkbox)
            │   ├── activity_main.xml              — Layout utama (DrawerLayout, CoordinatorLayout, toolbar, fragment container, now-playing sheet, NavigationView)
            │   ├── activity_playlist_detail.xml   — Layout detail playlist (toolbar nama playlist, RecyclerView lagu)
            │   ├── dialog_audio_formats.xml       — Dialog format audio (ScrollView + LinearLayout container untuk checkbox)
            │   ├── dialog_folder_input.xml        — Dialog input folder (EditText path folder)
            │   ├── dialog_folder_list.xml         — Dialog daftar folder (list + empty state)
            │   ├── fragment_albums.xml            — Layout daftar album (RecyclerView + empty state)
            │   ├── fragment_artists.xml           — Layout daftar artis (RecyclerView + empty state)
            │   ├── fragment_favorites.xml         — Layout favorit (RecyclerView + empty state)
            │   ├── fragment_folders.xml           — Layout folder (RecyclerView + empty state)
            │   ├── fragment_other_audio.xml       — Layout audio lain (RecyclerView + empty state)
            │   ├── fragment_playlist.xml          — Layout playlist (RecyclerView + FAB + empty state)
            │   ├── fragment_settings.xml          — Layout settings (ScrollView, card: Playback, Storage, Appearance, Documentation)
            │   ├── fragment_songs.xml             — Layout daftar lagu (RecyclerView + empty state)
            │   ├── fragment_system_picker.xml     — Layout system picker (MaterialCardView tap target)
            │   ├── item_folder.xml                — Item folder (text path + tombol close)
            │   ├── item_folder_list.xml           — Item folder list (path, jumlah file, emoji status)
            │   ├── item_format_checkbox.xml       — Item checkbox format audio dengan indikator warna playability
            │   ├── item_format_header.xml         — Item header grup format collapsible (⋁/⋀)
            │   ├── item_group.xml                 — Item grup album/artis (nama grup + jumlah item, 64dp height)
            │   ├── item_playlist.xml              — Item playlist (ikon queue, nama playlist, jumlah lagu)
            │   ├── item_song.xml                  — Item lagu (album art, judul, artis, durasi)
            │   └── view_now_playing_sheet.xml     — Layout now-playing sheet (cover, info strip, seekbar, kontrol, favorit, overflow)
            │
            ├── menu/
            │   ├── bottom_nav_menu.xml            — Navigation drawer menu (8 item navigasi + grup Settings)
            │   ├── now_playing_options_menu.xml   — Overflow menu now-playing (Tambah ke Playlist, Rincian, Bagikan, Hapus)
            │   ├── song_options_menu.xml          — Context menu long-press lagu (Favorit, Tambah ke Playlist)
            │   └── songs_menu.xml                 — Toolbar menu songs (SearchView, Sort)
            │
            ├── mipmap-anydpi-v26/
            │   ├── ic_launcher.xml                — Adaptive icon default (bg ungu + fg nota musik putih)
            │   ├── ic_launcher_round.xml          — Adaptive icon round default
            │   ├── ic_launcher_note.xml           — "Nota Musik" adaptive icon
            │   ├── ic_launcher_note_round.xml     — "Nota Musik" round adaptive icon
            │   ├── ic_launcher_note_dark.xml      — "Nota Musik (Gelap)" adaptive icon
            │   ├── ic_launcher_note_dark_round.xml— "Nota Musik (Gelap)" round adaptive icon
            │   ├── ic_launcher_play.xml           — "Putar" adaptive icon
            │   ├── ic_launcher_play_round.xml     — "Putar" round adaptive icon
            │   ├── ic_launcher_play_dark.xml      — "Putar (Gelap)" adaptive icon
            │   ├── ic_launcher_play_dark_round.xml— "Putar (Gelap)" round adaptive icon
            │   ├── ic_launcher_queue.xml          — "Antrian" adaptive icon
            │   ├── ic_launcher_queue_round.xml    — "Antrian" round adaptive icon
            │
            ├── values/
            │   ├── colors.xml                    — Definisi warna Material3 dark theme (primary #6750A4, surface #1C1B1F, green #4CAF50)
            │   ├── dimens.xml                    — Definisi dimensi (spacing 8/16/24/32dp, icon size, cover size, corner radius, elevation)
            │   ├── strings.xml                   — Semua string UI Bahasa Indonesia (label, navigasi, settings, playback, dokumentasi, folder)
            │   └── themes.xml                    — Theme AppTheme: Material3 Dark NoActionBar, kustom warna, status/nav bar transparan
            │
            └── xml/
                └── backup_rules.xml              — Aturan backup Android: include SharedPreferences & Room database
```
