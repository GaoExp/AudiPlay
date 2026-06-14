## 📁 Struktur Project

```
AudiPlay/
│
├── .github/workflows/
│   └── release.yml              — GitHub Actions CI: build APK & buat GitHub Release
│
├── AGENTS.md                    — Pedoman AI agent
├── CHANGELOG.md                 — Riwayat perubahan per release
├── PANDUAN.md                   — Panduan penggunaan lengkap
├── README.md                    — Ringkasan project & fitur
├── build.gradle                 — Root Gradle: deklarasi plugin AGP, repository
├── gradle.properties            — Gradle: AndroidX, Jetifier, JVM args
├── gradlew / gradlew.bat        — Gradle wrapper scripts
├── release.sh                   — Script GitHub release
├── settings.gradle              — Settings Gradle (include :app)
├── keystore.properties          — Konfigurasi signing release
│
├── gradle/wrapper/
│   └── gradle-wrapper.properties — Konfigurasi Gradle wrapper
│
└── app/
    ├── build.gradle             — Module: minSdk 26, targetSdk 35, Room/Media3/Material3
    ├── proguard-rules.pro       — Aturan ProGuard (kosong, belum ada custom rules)
    │
    └── src/main/
        ├── AndroidManifest.xml  — Permission audio/video/images/notifikasi/BT/baterai,
        │                           activity-alias ikon, 7 Activity, MusicService
        │
        ├── assets/
        │   ├── CHANGELOG.md     — Riwayat perubahan (in-app, Markdown)
        │   ├── PANDUAN.md       — Panduan penggunaan (in-app, Markdown)
        │   └── README.md        — Ringkasan project (in-app, Markdown)
        │
        ├── java/exp/miniplayer/
        │   ├── MainActivity.java         — DrawerLayout, BottomSheet now-playing,
        │   │                               service binding, PopupMenu, updateNavHeader()
        │   ├── MiniPlayerApp.java        — Application: dark mode, PlaylistFileWatcher
        │   │
        │   ├── adapter/
        │   │   ├── GroupAdapter.java          — RecyclerView grup album/artis
        │   │   ├── PlaylistAdapter.java       — RecyclerView daftar playlist
        │   │   ├── PlaylistDetailAdapter.java — RecyclerView lagu dalam playlist
        │   │   └── SongAdapter.java           — RecyclerView item Audio
        │   │
        │   ├── data/
        │   │   └── AudioRepository.java       — Jembatan Room DB, scanner, preferences
        │   │
        │   ├── database/
        │   │   ├── AppDatabase.java           — Room DB singleton
        │   │   ├── FavoriteDao.java           — DAO tabel favorites
        │   │   ├── FavoriteEntity.java        — Entity favorit
        │   │   ├── PlaylistDao.java           — DAO tabel playlists
        │   │   ├── PlaylistEntity.java        — Entity playlist
        │   │   ├── PlaylistSongDao.java       — DAO tabel playlist_songs
        │   │   └── PlaylistSongEntity.java    — Entity playlist_songs
        │   │
        │   ├── model/
        │   │   └── Audio.java                 — Model Parcelable track audio
        │   │
        │   ├── player/
        │   │   └── MusicPlayer.java           — Wrapper Media3 ExoPlayer
        │   │
        │   ├── service/
        │   │   └── MusicService.java          — Foreground service, notifikasi kontrol
        │   │
        │   ├── ui/
        │   │   ├── albums/
        │   │   │   └── AlbumsFragment.java        — Daftar album hasil scan
        │   │   ├── artists/
        │   │   │   └── ArtistsFragment.java       — Daftar artis hasil scan
        │   │   ├── audioformat/
        │   │   │   └── FormatAudioActivity.java   — Pengaturan format audio dgn grup collapsible & indikator playability
│   │   ├── documentation/
│   │   │   └── DocumentationActivity.java — Baca file .md dari assets, render Markdown (Markwon)
        │   │   ├── favorites/
        │   │   │   ├── FavoritesFragment.java     — Daftar lagu favorit
        │   │   │   └── FavoritesViewModel.java    — ViewModel data favorit via LiveData
        │   │   ├── folders/
        │   │   │   ├── FolderListActivity.java    — Kelola folder: mode Kontrol (drag) & mode Semua
        │   │   │   ├── FolderSettingsActivity.java— Atur folder diizinkan/dikecualikan via SAF
        │   │   │   └── FoldersFragment.java       — Folder musik per direktori induk
        │   │   ├── other_audio/
        │   │   │   └── OtherAudioFragment.java    — File audio non-musik (rekaman, dll)
        │   │   ├── playlist/
        │   │   │   ├── PlaylistDetailActivity.java    — Daftar lagu dalam playlist
        │   │   │   ├── PlaylistDetailViewModel.java   — ViewModel detail playlist
        │   │   │   ├── PlaylistFragment.java          — Kelola playlist (buat/rename/hapus)
        │   │   │   └── PlaylistViewModel.java         — ViewModel daftar playlist
        │   │   ├── settings/
        │   │   │   ├── SettingsFragment.java      — Pengaturan: screen-on, audio focus,
        │   │   │   │                                 batasi folder, format audio, ikon, dll
        │   │   │   └── SettingsViewModel.java     — ViewModel state pengaturan & preferensi
        │   │   ├── songs/
        │   │   │   ├── SongsFragment.java         — Semua lagu: search, sort, tap putar
        │   │   │   ├── SongsViewModel.java        — ViewModel daftar lagu dgn filter & sorting
        │   │   │   └── SongListActivity.java      — Daftar lagu dari artis/album/folder tertentu
        │   │   └── system_picker/
        │   │       └── SystemPickerFragment.java  — Impor audio via system file picker (SAF)
        │   │
        │   └── utils/
        │       ├── MusicScanner.java              — Pindai MediaStore dgn filter folder & format
        │       ├── PermissionHelper.java          — Handler izin runtime (audio, video, notif)
        │       ├── PlaylistIO.java                — Export/import playlist M3U + JSON via SAF
        │       ├── PlaylistFileWatcher.java       — FileObserver real-time untuk file .m3u
        │       ├── PlaylistScanner.java           — Auto-scan .m3u/.m3u8 dari folder musik
        │       ├── PreferencesManager.java        — Wrapper SharedPreferences
        │       ├── QueueHolder.java               — Singleton antrean pemutaran
        │       └── TimeUtils.java                 — Format milidetik ke string durasi
        │
        └── res/
            ├── anim/                         — Animasi transisi
            │   ├── slide_in_up.xml           — Slide up (300ms) untuk now-playing sheet
            │   ├── slide_out_down.xml        — Slide down (300ms) untuk now-playing sheet
            │   └── stay.xml                  — Animasi diam (300ms) — placeholder
            │
            ├── drawable/                     — Ikon & shape vector
            │   ├── drag_handle_background.xml     — Shape rounded rectangle (2dp) drag handle
            │   ├── ic_album_default.xml           — CD/disc placeholder album art
            │   ├── ic_arrow_down.xml              — Chevron bawah — tutup now-playing
            │   ├── ic_artist.xml                  — Siluet orang — navigasi Artists
            │   ├── ic_bg_purple.xml               — Rectangle ungu (108dp) — bg launcher tema ungu
            │   ├── ic_bg_surface.xml              — Rectangle gelap — bg launcher tema gelap
            │   ├── ic_close.xml                   — Ikon X — hapus folder di settings
            │   ├── ic_favorite.xml                — Hati solid — status favorit
            │   ├── ic_favorite_border.xml         — Hati outline — tambah favorit
            │   ├── ic_fg_note.xml                 — Nota musik putih — fg launcher "Nota Musik"
            │   ├── ic_fg_note_dark.xml            — Nota musik ungu — fg launcher "Nota Musik (Gelap)"
            │   ├── ic_fg_play.xml                 — Segitiga play putih — fg launcher "Putar"
            │   ├── ic_fg_play_dark.xml            — Segitiga play ungu — fg launcher "Putar (Gelap)"
            │   ├── ic_fg_queue.xml                — Antrian putih — fg launcher "Antrian"
            │   ├── ic_folder.xml                  — Folder — navigasi Folders
            │   ├── ic_launcher_background.xml     — Background ungu (108dp) default launcher
            │   ├── ic_launcher_foreground.xml     — Nota musik putih detail
            │   ├── ic_music_note.xml              — Nota musik — navigasi Songs
            │   ├── ic_other_audio.xml             — Nota + gelombang — navigasi Other Audio
            │   ├── ic_overflow.xml                — Tiga titik vertikal — overflow menu
            │   ├── ic_pause.xml                   — Dua garis vertikal — pause
            │   ├── ic_play.xml                    — Segitiga kanan — play
            │   ├── ic_playlist_add.xml            — Daftar + plus — tambah ke playlist
            │   ├── ic_queue_music.xml             — Daftar + nota — navigasi Playlists
            │   ├── ic_repeat.xml                  — Panah melingkar — repeat all
            │   ├── ic_repeat_one.xml              — Panah + angka 1 — repeat one
            │   ├── ic_search.xml                  — Kaca pembesar — search
            │   ├── ic_settings.xml                — Gear — navigasi Settings & sort
            │   ├── ic_shuffle.xml                 — Panah silang — shuffle
            │   ├── ic_skip_next.xml               — Dua panah kanan — next track
            │   ├── ic_skip_previous.xml           — Dua panah kiri — previous track
            │   └── ic_system_picker.xml           — Monitor — navigasi System Picker
            │
            ├── layout/                       — Layout XML
            │   ├── activity_documentation.xml     — Toolbar, card list, Markdown content
│   ├── dialog_icon_picker.xml         — GridView pemilih ikon aplikasi
│   ├── item_icon_picker.xml           — Item grid ikon (gambar + nama)
│   ├── toolbar_zoom.xml               — Kontrol zoom −/+ di toolbar
            │   ├── activity_folder_list.xml       — Toolbar, toggle Kontrol/Semua, RecyclerView
            │   ├── activity_folder_settings.xml   — RecyclerView folder + FAB
            │   ├── activity_format_audio.xml      — RecyclerView grup + checkbox
            │   ├── activity_main.xml              — DrawerLayout + CoordinatorLayout + fragment
            │   ├── activity_playlist_detail.xml   — Toolbar + RecyclerView lagu
            │   ├── activity_song_list.xml         — Toolbar + section_header + RecyclerView
            │   ├── nav_header.xml                 — Header Navigation Drawer
            │   ├── dialog_audio_formats.xml       — ScrollView + checkbox format
            │   ├── dialog_folder_input.xml        — EditText input folder
            │   ├── dialog_folder_list.xml         — Daftar folder + empty state
            │   ├── fragment_albums.xml            — section_header + RecyclerView + empty
            │   ├── fragment_artists.xml           — section_header + RecyclerView + empty
            │   ├── fragment_favorites.xml         — RecyclerView + empty state
            │   ├── fragment_folders.xml           — section_header + RecyclerView + empty
            │   ├── fragment_other_audio.xml       — RecyclerView + empty state
            │   ├── fragment_playlist.xml          — ConstraintLayout + RecyclerView + FAB
            │   ├── fragment_settings.xml          — ScrollView card settings
            │   ├── fragment_songs.xml             — ConstraintLayout + RecyclerView + empty
            │   ├── fragment_system_picker.xml     — MaterialCardView tap target
            │   ├── item_folder.xml                — Text path + tombol close
            │   ├── item_folder_list.xml           — Path, file count, emoji status
            │   ├── item_format_checkbox.xml       — Checkbox + indikator playability
            │   ├── item_format_header.xml         — Header grup collapsible
            │   ├── item_group.xml                 — Group album/artis + stats
            │   ├── item_playlist.xml              — Ikon queue + nama + stats
            │   ├── item_song.xml                  — Album art + judul + artis + durasi
            │   └── view_now_playing_sheet.xml     — Cover + seekbar + kontrol + overflow
            │
            ├── menu/                         — Menu XML
            │   ├── bottom_nav_menu.xml            — Navigation drawer (8 item + grup Settings)
            │   ├── now_playing_options_menu.xml   — Overflow: playlist, rincian, bagikan, hapus
            │   ├── song_options_menu.xml          — Context menu: favorit, playlist
            │   └── songs_menu.xml                 — Toolbar: SearchView, Sort
            │
            ├── mipmap-anydpi-v26/           — Adaptive icon launcher
            │   ├── ic_launcher.xml               — Default adaptive icon
            │   ├── ic_launcher_round.xml         — Default round
            │   ├── ic_launcher_note.xml          — "Nota Musik"
            │   ├── ic_launcher_note_round.xml
            │   ├── ic_launcher_note_dark.xml     — "Nota Musik (Gelap)"
            │   ├── ic_launcher_note_dark_round.xml
            │   ├── ic_launcher_play.xml          — "Putar"
            │   ├── ic_launcher_play_round.xml
            │   ├── ic_launcher_play_dark.xml     — "Putar (Gelap)"
            │   ├── ic_launcher_play_dark_round.xml
            │   ├── ic_launcher_queue.xml         — "Antrian"
            │   ├── ic_launcher_queue_round.xml
            │   ├── ic_launcher_ico01.xml — ico17.xml     — 17 ikon alternatif tambahan (+ round)
            │
            ├── values/                       — Resource values
            │   ├── colors.xml                    — Warna Material3 dark theme
            │   ├── dimens.xml                    — Dimensi (spacing, icon, cover, radius)
            │   ├── strings.xml                   — Semua string UI Bahasa Indonesia
            │   └── themes.xml                    — Theme Material3 Dark NoActionBar
            │
            └── xml/
                └── backup_rules.xml          — Aturan backup: SharedPreferences + Room DB
```

### Statistik Project

| Kategori | Jumlah |
|----------|-------:|
| Java source | 45 |
| Layout XML | 31 |
| Drawable XML | 50 |
| Values XML | 4 |
| Mipmap XML | 29 |
| Menu XML | 4 |
| Anim XML | 3 |
| XML lainnya (Manifest, backup) | 2 |
| Assets (md) | 3 |
| Root dokumen | 4 |
| Root konfigurasi | 5 |
| Gradle & wrapper | 7 |
| CI/CD | 1 |
| **Total file** | **188** |
| **Total direktori** | **49** |

---
