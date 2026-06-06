# Panduan Penggunaan Mini Player

## Daftar Isi

- [Persyaratan](#persyaratan)
- [Memulai](#memulai)
- [Navigasi](#navigasi)
- [Fitur](#fitur)
- [Izin](#izin)

## Persyaratan

- Android 8.0 (API 26) atau lebih baru
- Izin akses audio/media

## Memulai

1. Install APK Mini Player
2. Berikan izin akses audio saat diminta
3. Aplikasi akan otomatis memindai file audio di perangkat

## Navigasi

Aplikasi menggunakan **Navigation Drawer** (sidebar) yang bisa dibuka dengan:
- Tap ikon hamburger (☰) di kiri atas
- Swipe dari tepi kiri layar

Drawer terdiri dari 4 menu:

| Menu       | Ikon           | Fungsi                          |
|------------|----------------|---------------------------------|
| Songs      | Musik note     | Daftar semua lagu               |
| Favorites  | Heart          | Lagu yang ditandai favorit      |
| Playlists  | Queue music    | Daftar playlist                 |
| Settings   | Gear           | Pengaturan aplikasi             |

## Fitur

### Daftar Lagu (Songs)
- Menampilkan semua lagu yang terdeteksi
- Cari lagu dengan kolom pencarian
- Urutkan lagu (A-Z, Z-A, durasi, tanggal)
- Tap lagu untuk memutar
- Menu opsi (tambah ke favorit, tambah ke playlist)

### Favorit (Favorites)
- Menampilkan lagu yang ditandai favorit
- Tap untuk memutar
- Hapus dari favorit

### Playlist
- Buat playlist baru
- Rename playlist
- Hapus playlist
- Tambah/hapus lagu dari playlist
- Lihat detail isi playlist

### Mini Player Bar
- Bar persistent di bagian bawah saat lagu diputar
- Menampilkan judul, artis, dan album art
- Tombol play/pause untuk kontrol cepat
- Tap bar untuk membuka layar Now Playing penuh

### Now Playing
- Cover art placeholder
- Informasi lagu (judul, artis, album)
- Kontrol pemutaran: Play/Pause, Next, Previous
- Mode Shuffle
- Mode Repeat (Off, All, One)
- Progress bar & time display
- Seek slider

### Pemutaran Background
- Musik tetap berjalan saat aplikasi di-minimize
- Notifikasi dengan kontrol play/pause, next, previous
- Notifikasi dengan tampilan informasi lagu

### Pengaturan (Settings)
- **Keep Screen On** — Layar tetap menyala saat pemutaran
- **Default Repeat Mode** — Atur mode ulang default
- **Scan All Audio** — Pindai ulang semua file audio
- **Included Folders** — Folder yang dipindai
- **Excluded Folders** — Folder yang dilewati

## Izin

| Izin                          | Fungsi                                   |
|-------------------------------|------------------------------------------|
| `READ_MEDIA_AUDIO` (13+)      | Membaca file audio                       |
| `READ_EXTERNAL_STORAGE` (≤12) | Membaca storage (audio)                  |
| `POST_NOTIFICATIONS` (13+)    | Menampilkan notifikasi pemutaran         |
| `FOREGROUND_SERVICE`          | Menjalankan service background           |
| `FOREGROUND_SERVICE_MEDIA_PLAYBACK` | Service media playback            |
| `BLUETOOTH_CONNECT`           | Kontrol Bluetooth (headset)              |

---
