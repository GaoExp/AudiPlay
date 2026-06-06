# Struktur Project

```
AudiPlay/
├── AGENTS.md                 # Pedoman AI untuk pengembangan
├── build.gradle              # Build configuration root
├── settings.gradle           # Settings Gradle
├── gradle.properties         # Properties Gradle
├── gradlew / gradlew.bat     # Gradle wrapper
├── gradle/                   # Gradle wrapper files
├── local.properties          # Local SDK properties
│
├── app/
│   ├── build.gradle          # Module app configuration
│   ├── proguard-rules.pro    # ProGuard rules
│   ├── libs/                 # Library JARs
│   ├── schemas/              # Room database schemas
│   │
│   └── src/main/
│       ├── AndroidManifest.xml
│       ├── res/
│       │   ├── drawable/         # Ikon & asset grafis
│       │   ├── layout/           # Layout XML (activity, fragment, item, view)
│       │   ├── menu/             # Menu definitions
│       │   ├── values/           # strings, colors, themes, dimens, arrays
│       │   ├── mipmap-*/         # Launcher icons
│       │   └── xml/              # Backup rules
│       │
│       └── java/exp/miniplayer/
│           ├── MiniPlayerApp.java            # Application class
│           ├── MainActivity.java             # Activity utama
│           │
│           ├── adapter/
│           │   ├── SongAdapter.java          # Adapter daftar lagu
│           │   ├── PlaylistAdapter.java      # Adapter daftar playlist
│           │   └── PlaylistDetailAdapter.java # Adapter isi playlist
│           │
│           ├── data/
│           │   └── AudioRepository.java      # Repository audio
│           │
│           ├── database/
│           │   ├── AppDatabase.java          # Room database
│           │   ├── FavoriteDao.java          # DAO favorit
│           │   ├── FavoriteEntity.java       # Entity favorit
│           │   ├── PlaylistDao.java          # DAO playlist
│           │   ├── PlaylistEntity.java       # Entity playlist
│           │   ├── PlaylistSongDao.java      # DAO lagu playlist
│           │   └── PlaylistSongEntity.java   # Entity lagu playlist
│           │
│           ├── model/
│           │   └── Audio.java                # Model data audio
│           │
│           ├── player/
│           │   └── MusicPlayer.java          # Player wrapper
│           │
│           ├── service/
│           │   └── MusicService.java         # Foreground service
│           │
│           └── ui/
│               ├── songs/
│               │   ├── SongsFragment.java
│               │   └── SongsViewModel.java
│               ├── favorites/
│               │   ├── FavoritesFragment.java
│               │   └── FavoritesViewModel.java
│               ├── playlist/
│               │   ├── PlaylistFragment.java
│               │   ├── PlaylistViewModel.java
│               │   ├── PlaylistDetailActivity.java
│               │   └── PlaylistDetailViewModel.java
│               ├── nowplaying/
│               │   ├── NowPlayingActivity.java
│               │   └── NowPlayingViewModel.java
│               └── settings/
│                   ├── SettingsFragment.java
│                   └── SettingsViewModel.java
```

## Package Overview

| Package         | Fungsi                                         |
|-----------------|------------------------------------------------|
| adapter         | Adapter untuk RecyclerView                     |
| data            | Repository & data layer                        |
| database        | Room database, DAO, entity                     |
| model           | Model domain (Audio)                           |
| player          | Wrapper ExoPlayer untuk pemutaran              |
| service         | Foreground service untuk background playback   |
| ui.songs        | Daftar lagu dengan pencarian                   |
| ui.favorites    | Lagu favorit                                   |
| ui.playlist     | Playlist & detail playlist                     |
| ui.nowplaying   | Layar pemutaran sekarang                       |
| ui.settings     | Pengaturan aplikasi                            |
| utils           | Utility helper (scanner, prefs, permission)    |

---
